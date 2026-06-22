package com.example.trabalhopratico1;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.List;

public class ChamadoAdapter extends RecyclerView.Adapter<ChamadoAdapter.ViewHolder> {

    private final List<Chamado> localLista;
    private final View.OnClickListener clickListener;

    public ChamadoAdapter(List<Chamado> lista) {
        this.localLista = lista;
        this.clickListener = null;
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
        holder.txtLocal.setText(c.getLocal());
        holder.txtDescricao.setText(c.getDescricao());

        if (c.getSolucao() != null && !c.getSolucao().isEmpty()) {
            holder.txtSolucao.setVisibility(View.VISIBLE);
            holder.txtSolucao.setText("Solução: " + c.getSolucao());
        } else {
            holder.txtSolucao.setVisibility(View.GONE);
        }

        String detalhes = c.getData() + " - " + c.getStatus();
        holder.txtInfo.setText(detalhes);

        // Badge de status — cria novo drawable pra evitar ClassCastException
        holder.txtStatusBadge.setText(c.getStatus());
        GradientDrawable badgeBg = new GradientDrawable();
        badgeBg.setCornerRadius(24f);
        switch (c.getStatus()) {
            case "Concluído":
                badgeBg.setColor(0xFF4CAF50);
                break;
            case "Em andamento":
                badgeBg.setColor(0xFFFF9800);
                break;
            default:
                badgeBg.setColor(0xFFF44336);
                break;
        }
        holder.txtStatusBadge.setBackground(badgeBg);

        // Miniatura com subsampling
        String caminhoImagem = c.getCaminhoImagem();
        if (caminhoImagem != null && !caminhoImagem.isEmpty()) {
            File imgFile = new File(caminhoImagem);
            if (imgFile.exists()) {
                holder.imgThumbnail.setVisibility(View.VISIBLE);
                Bitmap bitmap = decodificarThumbnail(caminhoImagem, 120, 120);
                holder.imgThumbnail.setImageBitmap(bitmap);
            } else {
                holder.imgThumbnail.setVisibility(View.GONE);
            }
        } else {
            holder.imgThumbnail.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), AtendimentoActivity.class);
            intent.putExtra("chamado_selecionado", c);
            v.getContext().startActivity(intent);
        });
    }

    private Bitmap decodificarThumbnail(String caminho, int maxLargura, int maxAltura) {
        BitmapFactory.Options opcoes = new BitmapFactory.Options();
        opcoes.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(caminho, opcoes);

        int fatorEscala = 1;
        while (opcoes.outWidth / fatorEscala > maxLargura
                || opcoes.outHeight / fatorEscala > maxAltura) {
            fatorEscala *= 2;
        }

        opcoes.inJustDecodeBounds = false;
        opcoes.inSampleSize = fatorEscala;
        return BitmapFactory.decodeFile(caminho, opcoes);
    }

    @Override
    public int getItemCount() {
        return localLista.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitulo, txtInfo, txtDescricao, txtSolucao, txtLocal, txtStatusBadge;
        ImageView imgThumbnail;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitulo = itemView.findViewById(R.id.textViewItemTitulo);
            txtInfo = itemView.findViewById(R.id.textViewItemDataLocal);
            txtDescricao = itemView.findViewById(R.id.textViewItemDescricao);
            txtSolucao = itemView.findViewById(R.id.textViewItemSolucao);
            txtLocal = itemView.findViewById(R.id.textViewItemLocal);
            txtStatusBadge = itemView.findViewById(R.id.textViewStatusBadge);
            imgThumbnail = itemView.findViewById(R.id.imageViewThumbnail);
        }
    }
}
