package com.geekbrains.myweatherv3;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.Task;

import java.util.Objects;

public class DeveloperFragment extends Fragment {

//    // Используется, чтобы определить результат Activity регистрации через
//    // Google
//    private static final int RC_SIGN_IN = 40404;
//    private static final String TAG = "GoogleAuth";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View layout = inflater.inflate(R.layout.fragment_developer, container, false);
        setRetainInstance(true);

        Parcel parcel = MainActivity.getParcel();
        parcel.setCurrentFragmentName(Objects.requireNonNull(requireActivity().getSupportFragmentManager().findFragmentById(R.id.container)).getTag());

//        // Конфигурация запроса на регистрацию пользователя, чтобы получить
//        // идентификатор пользователя, его почту и основной профайл
//        // (регулируется параметром)
//        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestEmail()
//                .build();
//
//        // Получаем клиента для регистрации и данные по клиенту
//        googleSignInClient = GoogleSignIn.getClient(this, gso);
//
//        // Кнопка регистрации пользователя
//        buttonSignIn = findViewById(R.id.sign_in_button);
//        buttonSignIn.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    signIn();
//                }
//            }
//        );
//



        return layout;
    }

//    // Клиент для регистрации пользователя через Google
//    private GoogleSignInClient googleSignInClient;
//
//    // Кнопка регистрации через Google
//    private com.google.android.gms.common.SignInButton buttonSignIn;
//
//
//    // Получаем результаты аутентификации от окна регистрации пользователя
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == RC_SIGN_IN) {
//            // Когда сюда возвращается Task, результаты аутентификации уже
//            // готовы
//            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
//            handleSignInResult(task);
//        }
//    }

}
