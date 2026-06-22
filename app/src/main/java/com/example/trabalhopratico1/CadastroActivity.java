package com.example.trabalhopratico1;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CadastroActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_CAMERA_PERMISSION = 100;

    private EditText campoTitulo, campoLocal, campoDescricao;
    private Spinner comboTipo, comboStatus;
    private Button btnSalvar, btnCapturarFoto, btnVoltar;
    private ImageView imagePreview;

    private String caminhoFotoAtual = null;
    private Uri fotoUri = null;

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

        campoTitulo = findViewById(R.id.editTextText);
        campoLocal = findViewById(R.id.editTextText2);
        campoDescricao = findViewById(R.id.editTextTextMultiLine);
        comboTipo = findViewById(R.id.spinner);
        comboStatus = findViewById(R.id.spinnerStatus);
        btnSalvar = findViewById(R.id.salvarchamado);
        btnCapturarFoto = findViewById(R.id.btnCapturarFoto);
        btnVoltar = findViewById(R.id.btnVoltar);
        imagePreview = findViewById(R.id.imageViewPreview);

        btnCapturarFoto.setOnClickListener(v -> verificarPermissaoCamera());
        btnSalvar.setOnClickListener(v -> salvarChamado());
        btnVoltar.setOnClickListener(v -> finish());
    }

    private void verificarPermissaoCamera() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.CAMERA},
                    REQUEST_CAMERA_PERMISSION);
        } else {
            abrirCamera();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                abrirCamera();
            } else {
                Toast.makeText(this, "Permissão de câmera necessária para registrar o chamado",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    private void abrirCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = criarArquivoImagem();
            } catch (IOException e) {
                Toast.makeText(this, "Erro ao criar arquivo de imagem", Toast.LENGTH_SHORT).show();
                return;
            }

            if (photoFile != null) {
                fotoUri = FileProvider.getUriForFile(this,
                        getApplicationContext().getPackageName() + ".fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fotoUri);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        } else {
            Toast.makeText(this, "Nenhum app de câmera disponível", Toast.LENGTH_SHORT).show();
        }
    }

    private File criarArquivoImagem() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "CHAMADO_" + timeStamp;

        File storageDir = new File(getFilesDir(), "imagens");
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }

        File image = new File(storageDir, imageFileName + ".jpg");
        image.createNewFile();
        caminhoFotoAtual = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if (resultCode == RESULT_OK) {
                imagePreview.setVisibility(View.VISIBLE);
                Bitmap bitmap = decodificarImagemReduzida(caminhoFotoAtual, 400, 400);
                imagePreview.setImageBitmap(bitmap);
                Toast.makeText(this, "Foto capturada com sucesso!", Toast.LENGTH_SHORT).show();
            } else {
                caminhoFotoAtual = null;
                fotoUri = null;
            }
        }
    }

    /**
     * Decodifica imagem com subsampling para evitar ANR na UI thread.
     */
    private Bitmap decodificarImagemReduzida(String caminho, int maxLargura, int maxAltura) {
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

    private void salvarChamado() {
        String titulo = campoTitulo.getText().toString();
        String local = campoLocal.getText().toString();
        String tipo = comboTipo.getSelectedItem().toString();
        String descricao = campoDescricao.getText().toString();
        String status = comboStatus.getSelectedItem().toString();

        if (titulo.isEmpty()) {
            campoTitulo.setError("O título é obrigatório!");
            return;
        }

        if (caminhoFotoAtual == null || caminhoFotoAtual.isEmpty()) {
            Toast.makeText(this, "É obrigatório capturar uma foto do problema!", Toast.LENGTH_LONG).show();
            return;
        }

        String dataAtual = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());

        Chamado novoChamado = new Chamado(titulo, local, tipo, descricao, dataAtual);
        novoChamado.setStatus(status);
        novoChamado.setCaminhoImagem(caminhoFotoAtual);

        DBHelper db = new DBHelper(CadastroActivity.this);
        db.salvarChamado(novoChamado);

        Toast.makeText(CadastroActivity.this, "Chamado salvo em " + dataAtual, Toast.LENGTH_SHORT).show();
        finish();
    }
}
