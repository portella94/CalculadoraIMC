package com.chrystianportella.calculadoraimc;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Executor;

import android.app.KeyguardManager;

import android.content.Intent;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;


public class MainActivity extends AppCompatActivity {
    LinearLayout _layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        _layout = findViewById(R.id.layout);
        testaAutenticacao();
        renderChart();
    }

    public void btnCalcularOnClick(View v) {

        TextView lblResultado = (TextView) findViewById(R.id.lblResultado);
        EditText txtPeso = (EditText) findViewById(R.id.txtPeso);
        EditText txtAltura = (EditText) findViewById(R.id.txtAltura);

        float peso = Integer.parseInt(txtPeso.getText().toString());
        float altura = Float.parseFloat(txtAltura.getText().toString());

        float resultado = peso / (altura * altura);
        // if (resultado < 19) {
        //     //abaixo
        //     lblResultado.setText("Abaixo do peso!");
        // } else if (resultado > 32) {
        //     //obeso
        //     lblResultado.setText("Acima do peso!");
        // } else {
        //     //ok
        //     lblResultado.setText("Peso ok!");
        // }
        Banco banco = new Banco(this);
        if (banco.InsertSQL(peso, altura))
            Toast.makeText(getApplicationContext(), "Inserido no histórico com sucesso!", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(getApplicationContext(), "Erro ao inserir no histórico!", Toast.LENGTH_SHORT).show();
        renderChart();
    }

    public void testaAutenticacao() {
        Executor ex = ContextCompat.getMainExecutor(this);
        BiometricManager bm = BiometricManager.from(this);

        if (bm.canAuthenticate() == BiometricManager.BIOMETRIC_SUCCESS) {
            autenticaUsuario(ex);
        } else {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                KeyguardManager km = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);

                if (km.isKeyguardSecure()) {
                    Intent authIntent = km.createConfirmDeviceCredentialIntent("Realize o Login usando suas credenciais", "");
                    startActivityForResult(authIntent, 3456);
                }
            }
        }
    }


    private void autenticaUsuario(Executor ex) {

        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Login com Biometria")
                .setSubtitle("Realize o Login usando suas credenciais")
                .setDescription("")
                .setDeviceCredentialAllowed(true)
                .build();

        BiometricPrompt biometricPrompt = new BiometricPrompt(this, ex, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode,
                                              @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(getApplicationContext(), "Erro de autenticação: " + errString, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationSucceeded(
                    @NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Toast.makeText(getApplicationContext(), "Autenticação realizada com sucesso!", Toast.LENGTH_SHORT).show();
                _layout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(getApplicationContext(), "Falha na autenticação", Toast.LENGTH_SHORT).show();
            }
        });

        biometricPrompt.authenticate(promptInfo);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1234) {
            testaAutenticacao();
        } else if (requestCode == 3456) {
            Toast.makeText(getApplicationContext(), "Autenticação realizada com sucesso!", Toast.LENGTH_SHORT).show();
            _layout.setVisibility(View.VISIBLE);
        }
    }

    public void renderChart() {
        BarChart chart = findViewById(R.id.chart);
        Banco banco = new Banco(this);
        //XAxis xAxis = chart.getXAxis();
        ArrayList<IMC> history = banco.getAll();
        //SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        //String[] dias = new String[history.size()];
        List<BarEntry> list = new ArrayList<>();
        if (history.size() > 0) {
            for (int i = 0; i < history.size(); i++) {
                IMC imc = history.get(i);
                //String s = sdf.format(imc.Data.getTime());
                //dias[i] = s;
                list.add(new BarEntry(
                        imc.Data.get(Calendar.DAY_OF_MONTH),
                        (float) history.get(i).IMC()
                ));
            }
            //xAxis.setValueFormatter(new IndexAxisValueFormatter(dias));
            BarDataSet cjd = new BarDataSet(list, "IMC");
            cjd.setColor(Color.RED);
            BarData barData = new BarData();
            barData.addDataSet(cjd);
            chart.setData(barData);
        }

        chart.invalidate();
    }


}