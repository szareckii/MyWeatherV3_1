package com.geekbrains.myweatherv3.customview;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.MotionEvent;
import androidx.annotation.Nullable;
import com.geekbrains.myweatherv3.R;

public class ThermometerView extends View {

    // Цвет градусника
    private int batteryColor = Color.GRAY;
//    private int batteryColor = R.color.thermometer;
    // Цвет уровня градусника
    private int levelColor = Color.RED;
    // Цвет уровня градусника при нажатии
    private int levelPressedColor = Color.GREEN;
    // Изображение батареи
    private RectF batteryRectangle = new RectF();
    // Изображение уровня заряда
    private Rect levelRectangle = new Rect();
    // "Краска" уровня заряда при касании +
    private Paint levelPressedPaint;
    // Изображение носика градусника
    private int cx, cy;
    private int width;
    private int height;

    // "Краска" градусника
    private Paint ThermometerPaint;
    // "Краска" ртути
    private Paint levelPaint;
    // Уровень температуры
    private int level = 70;

    // Константы
    // Отступ элементов
    private final static int padding = 10;
    // Скругление углов батареи
    private final static int round = 20;
    //Радиус носика градусника
    private final static int radius = 25;
    //Радиус капли "ртути" в носике градусника
    private final static int radiusRed = 15;
    private final static int minTemp = -50;
    private final static int maxTemp = 50;

    private int temp;

    public ThermometerView(Context context) {
        super(context);
        init();
    }

    // Вызывается при добавлении элемента в макет
    // AttributeSet attrs - набор параметров, указанных в макете для этого
    // элемента
    public ThermometerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttr(context, attrs);
        init();
    }

    // Вызывается при добавлении элемента в макет с установленными стилями
    // AttributeSet attrs - набор параметров, указанных в макете для этого
    // элемента
    // int defStyleAttr - базовый установленный стиль
    public ThermometerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttr(context, attrs);
        init();
    }

    // Вызывается при добавлении элемента в макет с установленными стилями
    // AttributeSet attrs - набор параметров, указанных в макете для этого
    // элемента
    // int defStyleAttr - базовый установленный стиль
    // int defStyleRes - ресурс стиля, если он не был определён в предыдущем параметре
    public ThermometerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initAttr(context, attrs);
        init();
    }

    // Инициализация атрибутов пользовательского элемента из xml
    @SuppressLint("ResourceAsColor")
    private void initAttr(Context context, AttributeSet attrs){
        // При помощи этого метода получаем доступ к набору атрибутов.
        // На выходе - массив со значениями
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ThermometerView, 0, 0);

        // Чтобы получить какое-либо значение из этого массива,
        // надо вызвать соответствующий метод и передать в этот метод имя
        // ресурса, указанного в файле определения атрибутов (attrs.xml)
        // Вторым параметром идёт значение по умолчанию. Оно будет подставлено,
        // если атрибут не будет указан в макете
        batteryColor = typedArray.getColor(R.styleable.ThermometerView_battery_color, Color.GRAY);
//         batteryColor = typedArray.getColor(R.styleable.ThermometerView_battery_color, R.color.thermometer);

        levelColor = typedArray.getColor(R.styleable.ThermometerView_level_color, Color.RED);

        levelPressedColor = typedArray.getColor(R.styleable.ThermometerView_level_pressed_color, Color.GREEN);

        // Уровень температуры
        int level = typedArray.getInteger(R.styleable.ThermometerView_level, 100);

        // В конце работы дадим сигнал, что массив со значениями атрибутов
        // больше не нужен. Система в дальнейшем будет переиспользовать этот
        // объект, и мы больше не получим к нему доступ из этого элемента
        typedArray.recycle();
    }

    // Начальная инициализация полей класса
    @SuppressLint("ResourceAsColor")
    private void init(){
        ThermometerPaint = new Paint();
        ThermometerPaint.setColor(batteryColor);
//        batteryPaint.setARGB(80, batteryColor);
        ThermometerPaint.setStyle(Paint.Style.FILL);
        levelPaint = new Paint();
        levelPaint.setColor(levelColor);
        levelPaint.setStyle(Paint.Style.FILL);
        // Задаём "краску" для нажатия на элемент +
        levelPressedPaint = new Paint();
        levelPressedPaint.setColor(levelPressedColor);
        levelPressedPaint.setStyle(Paint.Style.FILL);
    }

    // Когда Android создаёт пользовательский экран, ещё не известны размеры
    // элемента, потому что используются расчётные единицы измерения: чтобы
    // элемент выглядел одинаково на разных устройствах, размер элемента
    // рассчитывается с учётом размера экрана, его разрешения и плотности
    // пикселей. Каждый раз при отрисовке экрана возникает необходимость
    // изменить размер элемента. Если нам надо нарисовать свой элемент,
    // переопределяем этот метод и задаём новые размеры элементов внутри View
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // Получаем реальные ширину и высоту
        // Ширина элемента
        width = w - getPaddingLeft() - getPaddingRight();
        // Высота элемента
        height = h - getPaddingTop() - getPaddingBottom();

        // Отрисовка тела градусника
        batteryRectangle.set((int)(2.5f * padding),
                padding,
                width - (int)(2.5f * padding),
                height - padding);

        //получение координат для носика
        cx = width / 2;
        cy = height - radius;
    }

    // Вызывается, когда надо нарисовать элемент
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawRoundRect(batteryRectangle, round, round, ThermometerPaint);
        canvas.drawCircle(cx, cy, radius, ThermometerPaint);
        canvas.drawRect(levelRectangle, levelPaint);
        canvas.drawCircle(cx, cy, radiusRed, levelPaint);
    }

    public void setTemperature(float temp) {
        if (temp > maxTemp) {
            temp = maxTemp;
        } else if (temp < minTemp) {
            temp = minTemp;
        }

        if (temp <= 0) {
            levelPaint.setColor(getResources().getColor(R.color.colorBlue));
        } else if (temp > 0 && temp < 20) {
            levelPaint.setColor(getResources().getColor(R.color.colorYellow));
        }
        levelPaint.setStyle(Paint.Style.FILL);

        // Отрисовка ртутного столба градусника
        levelRectangle.set(width / 2  - 3,
                (int) (height / 2 - (temp / maxTemp) * (height / 2) - padding),
                (width / 2 + 3),
                height - padding);
        invalidate();
    }
}
