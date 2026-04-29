package com.example.trabalhopratico1;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ListagemActivity extends AppCompatActivity {

    private ChamadoAdapter adapter;
    private List<Chamado> listaExibida = new ArrayList<>();
    private List<Chamado> listaCompletaBanco = new ArrayList<>();
    private Spinner spFiltro;
    private Button btnFiltroData;

    private String dataFiltro = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listagem);

        RecyclerView rv = findViewById(R.id.recyclerViewChamados);
        spFiltro = findViewById(R.id.spinnerFiltroStatus);
        btnFiltroData = findViewById(R.id.btnFiltroData);
        Button btnVoltar = findViewById(R.id.btnVoltarInicial);

        rv.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ChamadoAdapter(listaExibida);
        rv.setAdapter(adapter);

        spFiltro.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                aplicarFiltroGeral();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        btnFiltroData.setOnClickListener(v -> abrirCalendario());

        btnFiltroData.setOnLongClickListener(v -> {
            dataFiltro = "";
            btnFiltroData.setText("Data (Todas)");
            aplicarFiltroGeral();
            Toast.makeText(ListagemActivity.this, "Filtro de data removido", Toast.LENGTH_SHORT).show();
            return true; // Indica que consumimos o clique longo
        });

        btnVoltar.setOnClickListener(v -> finish());
    }

    private void abrirCalendario() {
        Calendar calendario = Calendar.getInstance();
        int ano = calendario.get(Calendar.YEAR);
        int mes = calendario.get(Calendar.MONTH);
        int dia = calendario.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {

            dataFiltro = String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year);
            btnFiltroData.setText(dataFiltro); // Mostra a data no botão
            aplicarFiltroGeral(); // Aplica o filtro
        }, ano, mes, dia);

        dialog.show();
    }

    private void aplicarFiltroGeral() {
        if (listaCompletaBanco == null) return;

        listaExibida.clear();
        String statusSelecionado = spFiltro.getSelectedItem().toString();

        for (Chamado c : listaCompletaBanco) {
            boolean passaStatus = statusSelecionado.equals("Todos") ||
                    c.getStatus().trim().equalsIgnoreCase(statusSelecionado.trim());

            boolean passaData = dataFiltro.isEmpty() ||
                    c.getData().trim().equals(dataFiltro.trim());

            if (passaStatus && passaData) {
                listaExibida.add(c);
            }
        }

        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        DBHelper db = new DBHelper(this);
        listaCompletaBanco = db.listarTodos();

        if (adapter != null && spFiltro.getSelectedItem() != null) {
            aplicarFiltroGeral();
        }
    }
}