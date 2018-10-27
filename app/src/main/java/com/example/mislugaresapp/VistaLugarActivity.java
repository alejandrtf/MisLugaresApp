package com.example.mislugaresapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.DateFormat;
import java.util.Date;

import static android.content.Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION;
import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;
import static android.support.v4.content.FileProvider.getUriForFile;

public class VistaLugarActivity extends AppCompatActivity {
    final static int RESULTADO_EDITAR = 1;
    final static int RESULTADO_GALERIA = 2;
    final static int RESULTADO_FOTO = 3;

    private long id;
    private Lugar lugar;
    private ImageView imageView;
    private Uri uriFoto;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vista_lugar);

        Bundle extras = getIntent().getExtras();
        id = extras.getLong("id", -1);
        lugar = MainActivity.lugares.elemento((int) id);
        imageView = findViewById(R.id.foto);
        actualizarVistas();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.vista_lugar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.accion_compartir:
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, lugar.getNombre() + " - " + lugar.getUrl());
                startActivity(intent);
                return true;
            case R.id.accion_llegar:
                verMapa(null);
                return true;
            case R.id.accion_editar:
                lanzarEditarLugar(null);
                return true;
            case R.id.accion_borrar:
                borrarLugar((int) id);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void borrarLugar(final int id) {

        new AlertDialog.Builder(this)
                .setTitle("Borrado de lugar")
                .setMessage("Estás seguro que quieres eliminar este lugar?")
                .setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        MainActivity.lugares.borrar((int) id);
                        finish();

                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }


    public void lanzarEditarLugar(View v) {
        Intent i = new Intent(this, EdicionLugar.class);
        i.putExtra("id", id);
        startActivityForResult(i, RESULTADO_EDITAR);
    }


    public void actualizarVistas() {
        TextView nombre = findViewById(R.id.nombre);
        nombre.setText(lugar.getNombre());
        ImageView logo_tipo = findViewById(R.id.logo_tipo);
        logo_tipo.setImageResource(lugar.getTipo().getRecurso());
        TextView tipo = findViewById(R.id.tipo);
        tipo.setText(lugar.getTipo().getTexto());
        TextView direccion = findViewById(R.id.direccion);
        if (lugar.getDireccion().isEmpty()) {
            direccion.setVisibility(View.GONE);
        } else {
            direccion.setVisibility(View.VISIBLE);
            direccion.setText(lugar.getDireccion());
        }

        TextView telefono = findViewById(R.id.telefono);
        if (lugar.getTelefono() == 0) {
            telefono.setVisibility(View.GONE);
        } else {
            telefono.setVisibility(View.VISIBLE);
            telefono.setText(Integer.toString(lugar.getTelefono()));
        }
        TextView url = findViewById(R.id.url);
        if (lugar.getUrl().isEmpty()) {
            url.setVisibility(View.GONE);
        } else {
            url.setVisibility(View.VISIBLE);
            url.setText(lugar.getUrl());
        }

        TextView comentario = findViewById(R.id.comentario);
        comentario.setText(lugar.getComentario());
        TextView fecha = findViewById(R.id.fecha);
        fecha.setText(DateFormat.getDateInstance().format(
                new Date(lugar.getFecha())));
        TextView hora = findViewById(R.id.hora);
        hora.setText(DateFormat.getTimeInstance().format(
                new Date(lugar.getFecha())));
        RatingBar valoracion = findViewById(R.id.valoracion);
        valoracion.setRating(lugar.getValoracion());
        valoracion.setOnRatingBarChangeListener(
                new RatingBar.OnRatingBarChangeListener() {
                    @Override
                    public void onRatingChanged(RatingBar ratingBar, float valor, boolean fromUser) {
                        lugar.setValoracion(valor);
                    }
                });
        ponerFoto(imageView, lugar.getFoto());

    }


    public void verMapa(View view) {
        Uri uri;
        double lat = lugar.getPosicion().getLatitud();
        double lon = lugar.getPosicion().getLongitud();
        if (lat != 0 || lon != 0) {
            uri = Uri.parse("geo: " + lat + "," + lon);
        } else {
            uri = Uri.parse("geo:0,0?q=" + lugar.getDireccion());
        }
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    public void llamadaTelefono(View vie) {
        startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + lugar.getTelefono())));
    }


    public void pgWeb(View view) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(lugar.getUrl())));
    }


    public void galeria(View view) {
        String action;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            action = Intent.ACTION_OPEN_DOCUMENT;
        } else {
            action = Intent.ACTION_PICK;
        }

        Intent intent = new Intent(action, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");

        startActivityForResult(intent, RESULTADO_GALERIA);
    }

    protected void ponerFoto(ImageView imageView, String uri) {
        if (uri != null && !uri.isEmpty() && !uri.equals("null")) {
            imageView.setImageBitmap(reduceBitmap(this,uri,1024,1024));
        } else {
            imageView.setImageBitmap(null);
        }
    }


    public void tomarFoto(View view) {

        String fileName = "img_" + (System.currentTimeMillis() / 1000);

        File file = new File(Environment.getExternalStorageDirectory(), fileName);

        if (Build.VERSION.SDK_INT >= 24) {

            uriFoto = getUriForFile(this, "fileProvider", file); }

        else {

            uriFoto = Uri.fromFile(file);
        }

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uriFoto);
        startActivityForResult(intent, RESULTADO_FOTO);
    }


    public void eliminarFoto(View view){
        lugar.setFoto(null);
        ponerFoto(imageView,null);
    }



    public static Bitmap reduceBitmap(Context context, String uri, int maxAncho, int maxAlto){
        try{
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds=true;
            BitmapFactory.decodeStream(context.getContentResolver().openInputStream(Uri.parse(uri)),null,options);
            options.inSampleSize=(int)Math.max(Math.ceil(options.outWidth/maxAncho),
                    Math.ceil(options.outHeight));
            options.inJustDecodeBounds=false;
            return BitmapFactory.decodeStream(context.getContentResolver().openInputStream(Uri.parse(uri)),null,options);
        }catch (FileNotFoundException e){
            Toast.makeText(context, "Fichero/recurso no encontrado", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            return null;
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RESULTADO_EDITAR:

                actualizarVistas();
                findViewById(R.id.svLayout).invalidate();
                break;
            case RESULTADO_GALERIA:
                if (resultCode == RESULT_OK) {
                    lugar.setFoto(data.getDataString());
                    ponerFoto(imageView, lugar.getFoto());
                } else {
                    Toast.makeText(this, "Foto no cargada", Toast.LENGTH_SHORT).show();
                }
                break;
            case RESULTADO_FOTO:
                if(resultCode==RESULT_OK && lugar!=null && uriFoto!=null){
                    lugar.setFoto(uriFoto.toString());
                    ponerFoto(imageView,lugar.getFoto());
                }else{
                    Toast.makeText(this, "Error en captura", Toast.LENGTH_SHORT).show();
                }

        }
    }
}
