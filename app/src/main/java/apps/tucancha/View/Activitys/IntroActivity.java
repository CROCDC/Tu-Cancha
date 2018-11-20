package apps.tucancha.View.Activitys;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import apps.tucancha.R;
import apps.tucancha.View.Fragments.Walkthrough.*;
import com.github.paolorotolo.appintro.AppIntro;

public class IntroActivity extends AppIntro {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setNextArrowColor(Color.BLACK);
        setColorDoneText(Color.BLACK);
        setColorSkipButton(Color.BLACK);
        setNextArrowColor(Color.BLACK);
        setNavBarColor(R.color.negro);
        setSeparatorColor(Color.BLACK);
        setIndicatorColor(Color.BLACK,Color.GREEN);
        setBarColor(getResources().getColor(R.color.colorPrimary));



        addSlide(new IntroExplicacionFragment());
        addSlide(new IntroExplicacionFuncionamientoFragment());
        addSlide(new IntroExplicacionBotonesFragment());
        addSlide(new IntroExplicacionAgregarJugadorFragment());
        addSlide(new IntroExplicacionBorrarJugadorFragment());
        addSlide(new IntroExplicacionLapizFragment());
        addSlide(new IntroExplicacionGuardadoFragment());
        addSlide(new IntroExplicacionCompartirFragment());
        addSlide(new IntroExplicacionFinalFragment());





    }


    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        Intent intent = new Intent(IntroActivity.this,MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        Intent intent = new Intent(IntroActivity.this,MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
        // Do something when the slide changes.
    }
}
