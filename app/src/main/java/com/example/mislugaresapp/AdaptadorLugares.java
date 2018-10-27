package com.example.mislugaresapp;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

public class AdaptadorLugares extends RecyclerView.Adapter<AdaptadorLugares.ViewHolder> {
    protected Lugares lugares; //lista de lugares a mostrar
    //listener
    protected View.OnClickListener listener;

    public AdaptadorLugares(Lugares lugares) {
        this.lugares = lugares;
    }

    //creamos el ViewHolder con la vista de un elemento sin personalizar
    @Override
    public AdaptadorLugares.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //inflamos la vista desde el xml
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.elemento_lista, parent, false);
        v.setOnClickListener(listener);
        return new ViewHolder(v);
    }


    //usando como base el ViewHolder y lo personalizamos
    @Override
    public void onBindViewHolder(AdaptadorLugares.ViewHolder holder, int position) {
        Lugar lugar = lugares.elemento(position);
        holder.bindLugar(lugar);//método que personaliza el holder a partir del lugar

    }


    //indicamos el número de elementos de la lista
    @Override
    public int getItemCount() {
        return lugares.tamanyo();
    }


    //creamos nuestro ViewHolder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nombre, direccion;
        public ImageView foto;
        public RatingBar valoracion;

        //constructor
        public ViewHolder(View itemView) {
            super(itemView);
            nombre = itemView.findViewById(R.id.nombre);
            direccion = itemView.findViewById(R.id.direccion);
            foto = itemView.findViewById(R.id.foto);
            valoracion = itemView.findViewById(R.id.valoracion);
        }

        //personaliz este holder con los datos del lugar
        public void bindLugar(Lugar lugar) {
            this.nombre.setText(lugar.getNombre());
            this.direccion.setText(lugar.getDireccion());
            int id = R.drawable.otros;
            switch (lugar.getTipo()) {
                case RESTAURANTE:
                    id = R.drawable.restaurante;
                    break;
                case BAR:
                    id = R.drawable.bar;
                    break;
                case COPAS:
                    id = R.drawable.copas;
                    break;
                case ESPECTACULO:
                    id = R.drawable.espectaculos;
                    break;
                case HOTEL:
                    id = R.drawable.hotel;
                    break;
                case COMPRAS:
                    id = R.drawable.compras;
                    break;
                case EDUCACION:
                    id = R.drawable.educacion;
                    break;
                case DEPORTE:
                    id = R.drawable.deporte;
                    break;
                case NATURALEZA:
                    id = R.drawable.naturaleza;
                    break;
                case GASOLINERA:
                    id = R.drawable.gasolinera;
                    break;
            }
            this.foto.setImageResource(id);
            this.foto.setScaleType(ImageView.ScaleType.FIT_END);
            this.valoracion.setRating(lugar.getValoracion());
        }
    }

    //setter para el listener
    public void setOnClickListener(View.OnClickListener listener) {
        this.listener = listener;
    }
}
