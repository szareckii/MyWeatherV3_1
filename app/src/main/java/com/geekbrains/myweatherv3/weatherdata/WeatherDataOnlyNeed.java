package com.geekbrains.myweatherv3.weatherdata;

import android.annotation.SuppressLint;
import android.util.Log;
import com.geekbrains.myweatherv3.DataClassOfDays;
import com.geekbrains.myweatherv3.DataClassOfHours;
import com.geekbrains.myweatherv3.Parcel;
import com.geekbrains.myweatherv3.model.WeatherRequest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import retrofit2.Response;

public class WeatherDataOnlyNeed {
    private Parcel parcel;
    private static final String TAG = "myLogs";
    private Calendar cDayPlusOne, cDayPlusTwo, cDayPlusThree, cDayPlusFour, cDayPlusFive, cDayPlusSix,
            cDayPlusSeven, cPlusOneHour, cPlusTwoHours, cPlusThreeHours, cPlusFourHours,
            cPlusFiveHours, cPlusSixHours, cPlusSevenHours, cPlusEightHours, cPlusNineHours,
            cPlusTenHours, cPlusElevenHours, cPlusTwelveHours, cPlusThirteenHours, cPlusFourteenHours,
            cPlusFifteenHours, cPlusSixteenHours, cPlusSeventeenHours, cPlusEighteenHours,
            cPlusNineteenHours, cPlusTwentyHours, cPlusTwentyOneHours, cPlusTwentyTwoHours,
            cPlusTwentyThreeHours, cPlusTwentyFourHours, cPlusTwentyFiveHours, cPlusTwentySixHours,
            cPlusTwentySevenHours, cPlusTwentyEightHours, cPlusTwentyNineHours, cPlusThirtyHours,
            cPlusThirtyOneHours, cPlusThirtyTwoHours, cPlusThirtyThreeHours, cPlusThirtyFfourHours,
            cPlusThirtyFiveHours, cPlusThirtySixHours;
    private DateFormat df, dfHour;

    public WeatherDataOnlyNeed(Parcel parcel, Response<WeatherRequest> response) {
        this.parcel = parcel;
        displayWeather(response);
    }

    /*Метод записи погоды в View"*/
    private void displayWeather(Response<WeatherRequest> response) {
        assert response.body() != null;
        int ft = Math.round(response.body().getCurrent().getTemp());
        parcel.setTempCurrent(getStringTemp(ft));
        String wind = String.valueOf(response.body().getCurrent().getWind_speed());
        parcel.setWindNow(wind);
        String pressure = String.valueOf((int) Math.round(response.body().getCurrent().getPressure() / 1.333));
        parcel.setPressureNow(pressure);
        int degree = response.body().getCurrent().getWind_deg();
        parcel.setWindDegree(degree);
        parcel.setTypesWeather(response.body().getCurrent().getWeather()[0].getIcon());

        findCurrentHour();

        DataClassOfHours[] dataHours = getDataClassOfHours(response.body());
        parcel.setDataHours(dataHours);

        DataClassOfDays[] dataDays = getDataClassOfDays(response.body());
        parcel.setDataDays(dataDays);
        Log.e(TAG, "WeatherDataOnlyNeed-displayWeather");

    }

    private DataClassOfDays[] getDataClassOfDays(WeatherRequest weatherRequest) {

        int[] temp_day = new int[8];
        int[] temp_night = new int[8];

        for (int i = 0; i < 8; i++) {
            temp_day[i] = Math.round(weatherRequest.getDaily()[i].getTemp().getDay());
            temp_night[i] = Math.round(weatherRequest.getDaily()[i].getTemp().getNight());
        }

        /*заполения массива погоды на 7 дней*/
        return new DataClassOfDays[]{
                new DataClassOfDays(df.format(cDayPlusOne.getTime()), getStringTemp(temp_day[1]),
                        weatherRequest.getDaily()[1].getWeather()[0].getIcon(),
                        getStringTemp(temp_night[1])),
                new DataClassOfDays(df.format(cDayPlusTwo.getTime()), getStringTemp(temp_day[2]),
                        weatherRequest.getDaily()[2].getWeather()[0].getIcon(),
                        getStringTemp(temp_night[2])),
                new DataClassOfDays(df.format(cDayPlusThree.getTime()), getStringTemp(temp_day[3]),
                        weatherRequest.getDaily()[3].getWeather()[0].getIcon(),
                        getStringTemp(temp_night[3])),
                new DataClassOfDays(df.format(cDayPlusFour.getTime()), getStringTemp(temp_day[4]),
                        weatherRequest.getDaily()[4].getWeather()[0].getIcon(),
                        getStringTemp(temp_night[4])),
                new DataClassOfDays(df.format(cDayPlusFive.getTime()), getStringTemp(temp_day[5]),
                        weatherRequest.getDaily()[5].getWeather()[0].getIcon(),
                        getStringTemp(temp_night[5])),
                new DataClassOfDays(df.format(cDayPlusSix.getTime()), getStringTemp(temp_day[6]),
                        weatherRequest.getDaily()[6].getWeather()[0].getIcon(),
                        getStringTemp(temp_night[6])),
                new DataClassOfDays(df.format(cDayPlusSeven.getTime()), getStringTemp(temp_day[7]),
                        weatherRequest.getDaily()[7].getWeather()[0].getIcon(),
                        getStringTemp(temp_night[7]))};
    }

    private DataClassOfHours[] getDataClassOfHours(WeatherRequest weatherRequest) {
    /*почасова погода на 36, т.к. 12 почасовых прогнозо с возможностью выбора между прогнозами
    1, 2 или 3 часа, т.е. 12*3*/
        int[] temp_hour = new int[37];
        for (int i = 0; i < 37; i++) {
            temp_hour[i] = Math.round(weatherRequest.getHourly()[i].getTemp());
        }
        return new DataClassOfHours[]{
                new DataClassOfHours(dfHour.format(cPlusOneHour.getTime()),
                        weatherRequest.getHourly()[1].getWeather()[0].getIcon(),
                        getStringTemp(temp_hour[1])),
                new DataClassOfHours(dfHour.format(cPlusTwoHours.getTime()),
                        weatherRequest.getHourly()[2].getWeather()[0].getIcon(),
                        getStringTemp(temp_hour[2])),
                new DataClassOfHours(dfHour.format(cPlusThreeHours.getTime()),
                        weatherRequest.getHourly()[3].getWeather()[0].getIcon(),
                        getStringTemp(temp_hour[3])),
                new DataClassOfHours(dfHour.format(cPlusFourHours.getTime()),
                        weatherRequest.getHourly()[4].getWeather()[0].getIcon(),
                        getStringTemp(temp_hour[4])),
                new DataClassOfHours(dfHour.format(cPlusFiveHours.getTime()),
                        weatherRequest.getHourly()[5].getWeather()[0].getIcon(),
                        getStringTemp(temp_hour[5])),
                new DataClassOfHours(dfHour.format(cPlusSixHours.getTime()),
                        weatherRequest.getHourly()[6].getWeather()[0].getIcon(),
                        getStringTemp(temp_hour[6])),
                new DataClassOfHours(dfHour.format(cPlusSevenHours.getTime()),
                        weatherRequest.getHourly()[7].getWeather()[0].getIcon(),
                        getStringTemp(temp_hour[7])),
                new DataClassOfHours(dfHour.format(cPlusEightHours.getTime()),
                        weatherRequest.getHourly()[8].getWeather()[0].getIcon(),
                        getStringTemp(temp_hour[8])),
                new DataClassOfHours(dfHour.format(cPlusNineHours.getTime()),
                        weatherRequest.getHourly()[9].getWeather()[0].getIcon(),
                        getStringTemp(temp_hour[9])),
                new DataClassOfHours(dfHour.format(cPlusTenHours.getTime()),
                        weatherRequest.getHourly()[10].getWeather()[0].getIcon(),
                        getStringTemp(temp_hour[10])),
                new DataClassOfHours(dfHour.format(cPlusElevenHours.getTime()),
                        weatherRequest.getHourly()[11].getWeather()[0].getIcon(),
                        getStringTemp(temp_hour[11])),
                new DataClassOfHours(dfHour.format(cPlusTwelveHours.getTime()),
                        weatherRequest.getHourly()[12].getWeather()[0].getIcon(),
                        getStringTemp(temp_hour[12])),
                new DataClassOfHours(dfHour.format(cPlusThirteenHours.getTime()),
                        weatherRequest.getHourly()[13].getWeather()[0].getIcon(),
                        getStringTemp(temp_hour[13])),
                new DataClassOfHours(dfHour.format(cPlusFourteenHours.getTime()),
                        weatherRequest.getHourly()[14].getWeather()[0].getIcon(),
                        getStringTemp(temp_hour[14])),
                new DataClassOfHours(dfHour.format(cPlusFifteenHours.getTime()),
                        weatherRequest.getHourly()[15].getWeather()[0].getIcon(),
                        getStringTemp(temp_hour[15])),
                new DataClassOfHours(dfHour.format(cPlusSixteenHours.getTime()),
                        weatherRequest.getHourly()[16].getWeather()[0].getIcon(),
                        getStringTemp(temp_hour[16])),
                new DataClassOfHours(dfHour.format(cPlusSeventeenHours.getTime()),
                        weatherRequest.getHourly()[17].getWeather()[0].getIcon(),
                        getStringTemp(temp_hour[17])),
                new DataClassOfHours(dfHour.format(cPlusEighteenHours.getTime()),
                        weatherRequest.getHourly()[18].getWeather()[0].getIcon(),
                        getStringTemp(temp_hour[18])),
                new DataClassOfHours(dfHour.format(cPlusNineteenHours.getTime()),
                        weatherRequest.getHourly()[19].getWeather()[0].getIcon(),
                        getStringTemp(temp_hour[19])),
                new DataClassOfHours(dfHour.format(cPlusTwentyHours.getTime()),
                        weatherRequest.getHourly()[20].getWeather()[0].getIcon(),
                        getStringTemp(temp_hour[20])),
                new DataClassOfHours(dfHour.format(cPlusTwentyOneHours.getTime()),
                        weatherRequest.getHourly()[21].getWeather()[0].getIcon(),
                        getStringTemp(temp_hour[21])),
                new DataClassOfHours(dfHour.format(cPlusTwentyTwoHours.getTime()),
                        weatherRequest.getHourly()[22].getWeather()[0].getIcon(),
                        getStringTemp(temp_hour[22])),
                new DataClassOfHours(dfHour.format(cPlusTwentyThreeHours.getTime()),
                        weatherRequest.getHourly()[23].getWeather()[0].getIcon(),
                        getStringTemp(temp_hour[23])),
                new DataClassOfHours(dfHour.format(cPlusTwentyFourHours.getTime()),
                        weatherRequest.getHourly()[24].getWeather()[0].getIcon(),
                        getStringTemp(temp_hour[24])),
                new DataClassOfHours(dfHour.format(cPlusTwentyFiveHours.getTime()),
                        weatherRequest.getHourly()[25].getWeather()[0].getIcon(),
                        getStringTemp(temp_hour[25])),
                new DataClassOfHours(dfHour.format(cPlusTwentySixHours.getTime()),
                        weatherRequest.getHourly()[26].getWeather()[0].getIcon(),
                        getStringTemp(temp_hour[26])),
                new DataClassOfHours(dfHour.format(cPlusTwentySevenHours.getTime()),
                        weatherRequest.getHourly()[27].getWeather()[0].getIcon(),
                        getStringTemp(temp_hour[27])),
                new DataClassOfHours(dfHour.format(cPlusTwentyEightHours.getTime()),
                        weatherRequest.getHourly()[28].getWeather()[0].getIcon(),
                        getStringTemp(temp_hour[28])),
                new DataClassOfHours(dfHour.format(cPlusTwentyNineHours.getTime()),
                        weatherRequest.getHourly()[29].getWeather()[0].getIcon(),
                        getStringTemp(temp_hour[29])),
                new DataClassOfHours(dfHour.format(cPlusThirtyHours.getTime()),
                        weatherRequest.getHourly()[30].getWeather()[0].getIcon(),
                        getStringTemp(temp_hour[30])),
                new DataClassOfHours(dfHour.format(cPlusThirtyOneHours.getTime()),
                        weatherRequest.getHourly()[31].getWeather()[0].getIcon(),
                        getStringTemp(temp_hour[31])),
                new DataClassOfHours(dfHour.format(cPlusThirtyTwoHours.getTime()),
                        weatherRequest.getHourly()[32].getWeather()[0].getIcon(),
                        getStringTemp(temp_hour[32])),
                new DataClassOfHours(dfHour.format(cPlusThirtyThreeHours.getTime()),
                        weatherRequest.getHourly()[33].getWeather()[0].getIcon(),
                        getStringTemp(temp_hour[33])),
                new DataClassOfHours(dfHour.format(cPlusThirtyFfourHours.getTime()),
                        weatherRequest.getHourly()[34].getWeather()[0].getIcon(),
                        getStringTemp(temp_hour[34])),
                new DataClassOfHours(dfHour.format(cPlusThirtyFiveHours.getTime()),
                        weatherRequest.getHourly()[35].getWeather()[0].getIcon(),
                        getStringTemp(temp_hour[35])),
                new DataClassOfHours(dfHour.format(cPlusThirtySixHours.getTime()),
                        weatherRequest.getHourly()[36].getWeather()[0].getIcon(),
                        getStringTemp(temp_hour[36]))};
    }

    /*Метод добавления знака температуре*/
    private String getStringTemp(int ft) {
        String temp;
        if (ft > 0) {
            temp = "+";
        } else {
            temp = "-";
        }
        temp += String.valueOf(ft);
        return temp;
    }

    @SuppressLint("SimpleDateFormat")
    private void findCurrentHour() {
        cPlusOneHour = Calendar.getInstance();
        cPlusOneHour.set(Calendar.MINUTE, 0);
        cPlusOneHour.add(Calendar.HOUR, 1);

        cPlusTwoHours = Calendar.getInstance();
        cPlusTwoHours.set(Calendar.MINUTE, 0);
        cPlusTwoHours.add(Calendar.HOUR, 2) ;

        cPlusThreeHours = Calendar.getInstance();
        cPlusThreeHours.set(Calendar.MINUTE, 0);
        cPlusThreeHours.add(Calendar.HOUR, 3);

        cPlusFourHours = Calendar.getInstance();
        cPlusFourHours.set(Calendar.MINUTE, 0);
        cPlusFourHours.add(Calendar.HOUR, 4);

        cPlusFiveHours = Calendar.getInstance();
        cPlusFiveHours.set(Calendar.MINUTE, 0);
        cPlusFiveHours.add(Calendar.HOUR, 5);

        cPlusSixHours = Calendar.getInstance();
        cPlusSixHours.set(Calendar.MINUTE, 0);
        cPlusSixHours.add(Calendar.HOUR, 6);

        cPlusSevenHours = Calendar.getInstance();
        cPlusSevenHours.set(Calendar.MINUTE, 0);
        cPlusSevenHours.add(Calendar.HOUR, 7);

        cPlusEightHours = Calendar.getInstance();
        cPlusEightHours.set(Calendar.MINUTE, 0);
        cPlusEightHours.add(Calendar.HOUR, 8);

        cPlusNineHours = Calendar.getInstance();
        cPlusNineHours.set(Calendar.MINUTE, 0);
        cPlusNineHours.add(Calendar.HOUR, 9);

        cPlusTenHours = Calendar.getInstance();
        cPlusTenHours.set(Calendar.MINUTE, 0);
        cPlusTenHours.add(Calendar.HOUR, 10);

        cPlusElevenHours = Calendar.getInstance();
        cPlusElevenHours.set(Calendar.MINUTE, 0);
        cPlusElevenHours.add(Calendar.HOUR, 11);

        cPlusTwelveHours = Calendar.getInstance();
        cPlusTwelveHours.set(Calendar.MINUTE, 0);
        cPlusTwelveHours.add(Calendar.HOUR, 12);

        cPlusThirteenHours = Calendar.getInstance();
        cPlusThirteenHours.set(Calendar.MINUTE, 0);
        cPlusThirteenHours.add(Calendar.HOUR, 13);

        cPlusFourteenHours = Calendar.getInstance();
        cPlusFourteenHours.set(Calendar.MINUTE, 0);
        cPlusFourteenHours.add(Calendar.HOUR, 14) ;

        cPlusFifteenHours = Calendar.getInstance();
        cPlusFifteenHours.set(Calendar.MINUTE, 0);
        cPlusFifteenHours.add(Calendar.HOUR, 15);

        cPlusSixteenHours = Calendar.getInstance();
        cPlusSixteenHours.set(Calendar.MINUTE, 0);
        cPlusSixteenHours.add(Calendar.HOUR, 16);

        cPlusSeventeenHours = Calendar.getInstance();
        cPlusSeventeenHours.set(Calendar.MINUTE, 0);
        cPlusSeventeenHours.add(Calendar.HOUR, 17);

        cPlusEighteenHours = Calendar.getInstance();
        cPlusEighteenHours.set(Calendar.MINUTE, 0);
        cPlusEighteenHours.add(Calendar.HOUR, 18);

        cPlusNineteenHours = Calendar.getInstance();
        cPlusNineteenHours.set(Calendar.MINUTE, 0);
        cPlusNineteenHours.add(Calendar.HOUR, 19);

        cPlusTwentyHours = Calendar.getInstance();
        cPlusTwentyHours.set(Calendar.MINUTE, 0);
        cPlusTwentyHours.add(Calendar.HOUR, 20);

        cPlusTwentyOneHours = Calendar.getInstance();
        cPlusTwentyOneHours.set(Calendar.MINUTE, 0);
        cPlusTwentyOneHours.add(Calendar.HOUR, 21);

        cPlusTwentyTwoHours = Calendar.getInstance();
        cPlusTwentyTwoHours.set(Calendar.MINUTE, 0);
        cPlusTwentyTwoHours.add(Calendar.HOUR, 22);

        cPlusTwentyThreeHours = Calendar.getInstance();
        cPlusTwentyThreeHours.set(Calendar.MINUTE, 0);
        cPlusTwentyThreeHours.add(Calendar.HOUR, 23);

        cPlusTwentyFourHours = Calendar.getInstance();
        cPlusTwentyFourHours.set(Calendar.MINUTE, 0);
        cPlusTwentyFourHours.add(Calendar.HOUR, 24);

        cPlusTwentyFiveHours = Calendar.getInstance();
        cPlusTwentyFiveHours.set(Calendar.MINUTE, 0);
        cPlusTwentyFiveHours.add(Calendar.HOUR, 25);

        cPlusTwentySixHours = Calendar.getInstance();
        cPlusTwentySixHours.set(Calendar.MINUTE, 0);
        cPlusTwentySixHours.add(Calendar.HOUR, 26) ;

        cPlusTwentySevenHours = Calendar.getInstance();
        cPlusTwentySevenHours.set(Calendar.MINUTE, 0);
        cPlusTwentySevenHours.add(Calendar.HOUR, 27);

        cPlusTwentyEightHours = Calendar.getInstance();
        cPlusTwentyEightHours.set(Calendar.MINUTE, 0);
        cPlusTwentyEightHours.add(Calendar.HOUR, 28);

        cPlusTwentyNineHours = Calendar.getInstance();
        cPlusTwentyNineHours.set(Calendar.MINUTE, 0);
        cPlusTwentyNineHours.add(Calendar.HOUR, 29);

        cPlusThirtyHours = Calendar.getInstance();
        cPlusThirtyHours.set(Calendar.MINUTE, 0);
        cPlusThirtyHours.add(Calendar.HOUR, 30);

        cPlusThirtyOneHours = Calendar.getInstance();
        cPlusThirtyOneHours.set(Calendar.MINUTE, 0);
        cPlusThirtyOneHours.add(Calendar.HOUR, 31);

        cPlusThirtyTwoHours = Calendar.getInstance();
        cPlusThirtyTwoHours.set(Calendar.MINUTE, 0);
        cPlusThirtyTwoHours.add(Calendar.HOUR, 32);

        cPlusThirtyThreeHours = Calendar.getInstance();
        cPlusThirtyThreeHours.set(Calendar.MINUTE, 0);
        cPlusThirtyThreeHours.add(Calendar.HOUR, 33);

        cPlusThirtyFfourHours = Calendar.getInstance();
        cPlusThirtyFfourHours.set(Calendar.MINUTE, 0);
        cPlusThirtyFfourHours.add(Calendar.HOUR, 34);

        cPlusThirtyFiveHours = Calendar.getInstance();
        cPlusThirtyFiveHours.set(Calendar.MINUTE, 0);
        cPlusThirtyFiveHours.add(Calendar.HOUR, 35);

        cPlusThirtySixHours = Calendar.getInstance();
        cPlusThirtySixHours.set(Calendar.MINUTE, 0);
        cPlusThirtySixHours.add(Calendar.HOUR, 36);

        dfHour = new SimpleDateFormat("HH:mm");

        cDayPlusOne = Calendar.getInstance();
        cDayPlusOne.add(Calendar.DAY_OF_MONTH, 1);
        cDayPlusTwo = Calendar.getInstance();
        cDayPlusTwo.add(Calendar.DAY_OF_MONTH, 2);
        cDayPlusThree = Calendar.getInstance();
        cDayPlusThree.add(Calendar.DAY_OF_MONTH, 3);
        cDayPlusFour = Calendar.getInstance();
        cDayPlusFour.add(Calendar.DAY_OF_MONTH, 4);
        cDayPlusFive = Calendar.getInstance();
        cDayPlusFive.add(Calendar.DAY_OF_MONTH, 5);
        cDayPlusSix = Calendar.getInstance();
        cDayPlusSix.add(Calendar.DAY_OF_MONTH, 6);
        cDayPlusSeven = Calendar.getInstance();
        cDayPlusSeven.add(Calendar.DAY_OF_MONTH, 7);

        df = new SimpleDateFormat("dd/MM");
    }

}