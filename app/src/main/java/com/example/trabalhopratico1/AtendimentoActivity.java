package com.example.trabalhopratico1;

import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.BreakIterator;

public class AtendimentoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_atendimento);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Chamado chamado = (Chamado) getIntent().getSerializableExtra("chamado_selecionado");

        TextView tvTitulo = findViewById(R.id.txtDetTitulo);
        TextView tvDataLocal = findViewById(R.id.txtDetDataLocal);
        EditText editSolucao = findViewById(R.id.editSolucao);
        Spinner spinnerStatus = findViewById(R.id.spinnerStatus);
        Button btnFinalizar = findViewById(R.id.btnFinalizarAtendimento);

        if (chamado != null) {
            tvTitulo.setText("Problema: " + chamado.getTitulo());
            tvDataLocal.setText("Data: " + chamado.getData() + " | Local: " + chamado.getLocal());
        }

        btnFinalizar.setOnClickListener(v -> {
            String solucaoTexto = editSolucao.getText().toString();
            String novoStatus = spinnerStatus.getSelectedItem().toString();

            if (solucaoTexto.isEmpty()) {
                editSolucao.setError("Descreva o que foi feito!");
            } else {
                chamado.setSolucao(solucaoTexto);
                chamado.setStatus(novoStatus);

                DBHelper db = new DBHelper(AtendimentoActivity.this);

                db.atualizarChamado(chamado);

                Toast.makeText(this, "Atendimento registrado!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}