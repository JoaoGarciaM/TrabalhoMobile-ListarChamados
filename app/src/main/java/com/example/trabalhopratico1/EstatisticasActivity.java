package com.example.trabalhopratico1;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class EstatisticasActivity extends AppCompatActivity {

    private TextView txtTotal, txtAbertos, txtEmAndamento, txtConcluidos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estatisticas);

        txtTotal = findViewById(R.id.txtTotal);
        txtAbertos = findViewById(R.id.txtAbertos);
        txtEmAndamento = findViewById(R.id.txtEmAndamento);
        txtConcluidos = findViewById(R.id.txtConcluidos);

        findViewById(R.id.btnVoltar).setOnClickListener(v -> finish());

        carregarEstatisticas();
    }

    private void carregarEstatisticas() {
        int[] stats = new DBHelper(this).contarEstatisticas();
        txtTotal.setText(String.valueOf(stats[0]));
        txtAbertos.setText(String.valueOf(stats[1]));
        txtEmAndamento.setText(String.valueOf(stats[2]));
        txtConcluidos.setText(String.valueOf(stats[3]));
    }

    @Override
    protected void onResume() {
        super.onResume();
        carregarEstatisticas();
    }
}
