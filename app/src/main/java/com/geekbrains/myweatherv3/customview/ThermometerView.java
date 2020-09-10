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
    private float cx, cy;

    // "Краска" батареи
    private Paint batteryPaint;
    // "Краска" заряда
    private Paint levelPaint;

    // Касаемся элемента
    private boolean pressed = false;
    // Слушатель касания
    private OnClickListener listener;

    // Константы
    // Отступ элементов
    private final static int padding = 10;
    // Скругление углов батареи
    private final static int round = 20;
    // Ширина головы батареи
    private final static int headWidth = 10;
    //Радиус носика градусника
    private final static int radius = 25;
    //Радиус капли "ртути" в носике градусника
    private final static int radiusRed = 15;

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
        batteryColor = typedArray.getColor(R.styleable.ThermometerView_battery_color, Color.GRAY);

        // Вторым параметром идёт значение по умолчанию. Оно будет подставлено,
        // если атрибут не будет указан в макете
        levelColor = typedArray.getColor(R.styleable.ThermometerView_level_color, Color.RED);

        levelPressedColor = typedArray.getColor(R.styleable.ThermometerView_level_pressed_color, Color.GREEN);

        // Уровень заряда
        int level = typedArray.getInteger(R.styleable.ThermometerView_level, 100);

        // В конце работы дадим сигнал, что массив со значениями атрибутов
        // больше не нужен. Система в дальнейшем будет переиспользовать этот
        // объект, и мы больше не получим к нему доступ из этого элемента
        typedArray.recycle();
    }

    // Начальная инициализация полей класса
    @SuppressLint("ResourceAsColor")
    private void init(){
        batteryPaint = new Paint();
        batteryPaint.setColor(batteryColor);
        batteryPaint.setStyle(Paint.Style.FILL);
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
        int width = w - getPaddingLeft() - getPaddingRight();
        // Высота элемента
        int height = h - getPaddingTop() - getPaddingBottom();

        // Отрисовка тела градусника
        batteryRectangle.set(padding,
                padding,
                width - padding - headWidth,
                height - padding);

        // Отрисовка ртутного столба градусника
        levelRectangle.set((int)(2.2f * padding),
//                (int)((height)*((double)level/(double)100)),
                2 * padding,
                (int)(width - 3.2f * padding),
                height - padding);

        //получение координат для носика
        cx = padding + 15;
        cy = height - radius;
    }

    // Вызывается, когда надо нарисовать элемент
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRoundRect(batteryRectangle, round, round, batteryPaint);
        canvas.drawCircle(cx, cy, radius, batteryPaint);
        canvas.drawCircle(cx, cy, radiusRed, levelPaint);

        // Условие отрисовки (нажат или не нажат элемент) +
        if (pressed){
            canvas.drawRect(levelRectangle, levelPressedPaint);
        }
        else {
            canvas.drawRect(levelRectangle, levelPaint);
        }
    }

    // Этот метод срабатывает при касании элемента
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event){

        // Получаем действие (касание, отпускание, перемещение и т. д.)
        int Action = event.getAction();

        // Проверка на начало касания (элемент нажат)
        if(Action == MotionEvent.ACTION_DOWN){

            // Установим признак того, что нажали элемент
            pressed = true;

            // Вызываем метод для перерисовки
            invalidate();

            // Если слушатель был установлен, то вызываем его метод
            if (listener != null) {
                listener.onClick(this);
            }
        }

        // Проверка на отпускание элемента (палец убран)
        else if(Action == MotionEvent.ACTION_UP){

            // Снимаем признак касания элемента
            pressed = false;

            // Перерисовка элемента
            invalidate();
        }

        // Касание обработано, возвращаем true
        return true;
    }

    // Устанавливаем слушатель
    @Override
    public void setOnClickListener(View.OnClickListener listener){
        this.listener = listener;
    }


}
