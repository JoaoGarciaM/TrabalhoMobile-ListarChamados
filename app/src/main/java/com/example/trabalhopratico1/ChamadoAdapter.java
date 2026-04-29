package com.example.trabalhopratico1;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ChamadoAdapter extends RecyclerView.Adapter<ChamadoAdapter.ViewHolder> {

    private List<Chamado> localLista;

    public ChamadoAdapter(List<Chamado> lista) {
        this.localLista = lista;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chamado, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Chamado c = localLista.get(position);
        holder.txtTitulo.setText(c.getTitulo());
        holder.txtDescricao.setText(c.getDescricao());

        if (c.getSolucao() != null && !c.getSolucao().isEmpty()) {
            holder.txtSolucao.setVisibility(View.VISIBLE);
            holder.txtSolucao.setText("Solução: " + c.getSolucao());
        } else {
            holder.txtSolucao.setVisibility(View.GONE);
        }

        String detalhes = c.getData() + " - " + c.getLocal() + " (" + c.getStatus().toUpperCase() + ")";

        holder.txtInfo.setText(detalhes);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), AtendimentoActivity.class);

                // Passamos o chamado clicado para a próxima tela
                intent.putExtra("chamado_selecionado", c);

                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return localLista.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitulo, txtInfo, txtDescricao, txtSolucao;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitulo = itemView.findViewById(R.id.textViewItemTitulo);
            txtInfo = itemView.findViewById(R.id.textViewItemDataLocal);
            txtDescricao = itemView.findViewById(R.id.textViewItemDescricao);
            txtSolucao = itemView.findViewById(R.id.textViewItemSolucao);
        }
    }
}