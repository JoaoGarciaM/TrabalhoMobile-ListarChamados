package com.example.trabalhopratico1;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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

public class CadastroActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cadastro);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        EditText campoTitulo = findViewById(R.id.editTextText);
        EditText campoLocal = findViewById(R.id.editTextText2);
        EditText campoDescricao = findViewById(R.id.editTextTextMultiLine);
        Spinner comboTipo = findViewById(R.id.spinner);
        Button btnSalvar = findViewById(R.id.salvarchamado);


        btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String titulo = campoTitulo.getText().toString();
                String local = campoLocal.getText().toString();
                String tipo = comboTipo.getSelectedItem().toString();

                if (titulo.isEmpty()) {
                    campoTitulo.setError("O título é obrigatório!");
                } else {

                    String dataAtual = new java.text.SimpleDateFormat("dd/MM/yyyy").format(new java.util.Date());

                    String descricao = campoDescricao.getText().toString();

                    Chamado novoChamado = new Chamado(titulo, local, tipo, descricao, dataAtual);

                    DBHelper db = new DBHelper(CadastroActivity.this);

                    db.salvarChamado(novoChamado);

                    Toast.makeText(CadastroActivity.this, "Chamado salvo em " + dataAtual, Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
    }
}