package dev.com.br.gtechapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

import dev.com.br.gtechapp.R;
import dev.com.br.gtechapp.entity.Registro;
import dev.com.br.gtechapp.persistence.PrefsUsuario;

import static dev.com.br.gtechapp.service.Util.converteDataSqlParaBr;

public class RegistrosAdapter extends RecyclerView.Adapter<RegistrosAdapter.MyViewHolder>{

    private Context mContext;
    private List<Registro> listaRegistros;

    private PrefsUsuario prefsUsuario;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView txtDatRegistro, txtHorRegistro, txtValRegistro, txtEstadoExtraInfo, txtTipoRegistro, tvwUnidade;
        public ImageView imgIcon, imgStatus;
        public View vwTipoRegistro1, vwTipoRegistro2;

        public MyViewHolder(View view) {
            super(view);
            txtValRegistro = (TextView) view.findViewById(R.id.txtValResgistro);
            txtEstadoExtraInfo = (TextView) view.findViewById(R.id.txtEstadoExtraInfo);
            txtDatRegistro = (TextView) view.findViewById(R.id.txtDatRegistro);
            txtHorRegistro = (TextView) view.findViewById(R.id.txtHorRegistro);
            imgIcon = (ImageView) view.findViewById(R.id.imgIcon);
            txtTipoRegistro = (TextView) view.findViewById(R.id.txtTipoRegistro);
            vwTipoRegistro1 = (View) view.findViewById(R.id.vwTipoRegistro1);
            vwTipoRegistro2 = (View) view.findViewById(R.id.vwTipoRegistro2);
            imgStatus = (ImageView) view.findViewById(R.id.imgStatus);
            tvwUnidade = (TextView) view.findViewById(R.id.tvwUnidade);
        }
    }

    public RegistrosAdapter(Context mContext, List<Registro> listaRegistros) {
        this.mContext = mContext;
        this.listaRegistros = listaRegistros;
    }

    public RegistrosAdapter(Context mContext) {
        this.mContext = mContext;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_registro, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        Registro r = listaRegistros.get(position);

        prefsUsuario = new PrefsUsuario(mContext);

        int intHipo = prefsUsuario.getHipo() > 0 ? prefsUsuario.getHipo() : 70;
        int intHiper = prefsUsuario.getHiper() > 0 ? prefsUsuario.getHiper() : 140;

//        if(r.getNomEstadoAlimentacao() != null && !r.getNomEstadoAlimentacao().equals("")){
//            if(r.getNomEstadoAlimentacao().equals("Jejum")){
//                holder.imgStatus.setVisibility(View.VISIBLE);
//                if(r.getValRegistro() < intHipo){
//                    holder.imgStatus.setImageResource(R.drawable.meh);
//                }else if(r.getValRegistro() > intHiper){
//                    holder.imgStatus.setImageResource(R.drawable.sad);
//                }else{
//                    holder.imgStatus.setImageResource(R.drawable.happy);
//                }
//            }else if(r.getNomEstadoAlimentacao().equals("Pós Refeição")){
//                holder.imgStatus.setVisibility(View.VISIBLE);
//                if(r.getValRegistro() < intHipo){
//                    holder.imgStatus.setImageResource(R.drawable.meh);
//                }else if(r.getValRegistro() > intHiper){
//                    holder.imgStatus.setImageResource(R.drawable.sad);
//                }else{
//                    holder.imgStatus.setImageResource(R.drawable.happy);
//                }
//            } else{
//                holder.imgStatus.setVisibility(View.GONE);
//            }
//        } else{
//            holder.imgStatus.setVisibility(View.GONE);
//        }

        if(r.getTipRegistro() == 1){
            holder.imgStatus.setVisibility(View.VISIBLE);
            if(r.getValRegistro() < intHipo){
                holder.imgStatus.setImageResource(R.drawable.meh);
            }else if(r.getValRegistro() > intHiper){
                holder.imgStatus.setImageResource(R.drawable.sad);
            }else{
                holder.imgStatus.setImageResource(R.drawable.happy);
            }
        }else{
            holder.imgStatus.setVisibility(View.GONE);
        }

        if(r.getTipRegistro() == 1){

            holder.txtTipoRegistro.setText("GLICOSE");
            holder.txtTipoRegistro.setTextColor(Color.parseColor("#00437A"));
            holder.vwTipoRegistro1.setBackgroundColor(Color.parseColor("#00437A"));
            holder.vwTipoRegistro2.setBackground(mContext.getDrawable(R.drawable.blue_circle));
            holder.tvwUnidade.setVisibility(View.VISIBLE);
            holder.txtValRegistro.setTextSize(25);

        } else if (r.getTipRegistro() == 2){

            holder.txtTipoRegistro.setText("INSULINA");
            holder.txtTipoRegistro.setTextColor(Color.parseColor("#a4c639"));
            holder.vwTipoRegistro1.setBackgroundColor(Color.parseColor("#a4c639"));
            holder.vwTipoRegistro2.setBackground(mContext.getDrawable(R.drawable.gree_circle));
            holder.tvwUnidade.setVisibility(View.GONE);
//            holder.txtValRegistro.setTextSize(15);

        } else if (r.getTipRegistro() == 3){

            holder.txtTipoRegistro.setText("NOTA");
            holder.txtTipoRegistro.setTextColor(Color.parseColor("#D3D3D3"));
            holder.vwTipoRegistro1.setBackgroundColor(Color.parseColor("#D3D3D3"));
            holder.vwTipoRegistro2.setBackground(mContext.getDrawable(R.drawable.ligth_gray_circle));
            holder.tvwUnidade.setVisibility(View.GONE);
//            holder.txtValRegistro.setTextSize(15);

        }

        if(r.getValRegistro() != 0) {
            holder.txtValRegistro.setText(String.valueOf(r.getValRegistro()));
        } else if(r.getTxtNotaNota() != null){
            if(r.getTxtNotaNota().toString().trim().length() < 15){
                holder.txtValRegistro.setText(r.getTxtNotaNota().toString());
            }else{
                holder.txtValRegistro.setText(r.getTxtNotaNota().toString().substring(0, 15) + " ...");
            }
        } else {
            holder.txtValRegistro.setText("");
        }

        if(r.getTxtExtraInfo() != null){
            holder.txtEstadoExtraInfo.setText(r.getTxtExtraInfo());
        }else if(r.getCodTipoNota() == 1){
            holder.txtEstadoExtraInfo.setText("Nota");
        }else if(r.getCodTipoNota() == 2){
            holder.txtEstadoExtraInfo.setText("Nota de Refeição");
        }else if(r.getCodTipoNota() == 3){
            holder.txtEstadoExtraInfo.setText("Nota de Exercício");
        }else {
            holder.txtEstadoExtraInfo.setText("");
        }


        holder.txtDatRegistro.setText(converteDataSqlParaBr(r.getDatReg()));
        holder.txtHorRegistro.setText(" " + r.getHorRegistroSemSegundos());


        if(r.getTipInput() == 2){
            holder.imgIcon.setImageResource(R.drawable.ic_device);
            holder.imgIcon.setVisibility(View.VISIBLE);
        } else if(r.getCodTipoNota() == 1){
            holder.imgIcon.setImageResource(R.drawable.ic_book);
            holder.imgIcon.setVisibility(View.VISIBLE);
        } else if(r.getCodTipoNota() == 2){
            holder.imgIcon.setImageResource(R.drawable.ic_local_dining_black_24dp);
            holder.imgIcon.setVisibility(View.VISIBLE);
        } else if(r.getCodTipoNota() == 3){
            holder.imgIcon.setImageResource(R.drawable.ic_running);
            holder.imgIcon.setVisibility(View.VISIBLE);
        }else{
            holder.imgIcon.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        return listaRegistros.size();
    }
}
