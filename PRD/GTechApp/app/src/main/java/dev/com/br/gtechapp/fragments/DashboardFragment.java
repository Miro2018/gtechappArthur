package dev.com.br.gtechapp.fragments;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import dev.com.br.gtechapp.R;
import dev.com.br.gtechapp.entity.ContagemRegistros;
import dev.com.br.gtechapp.entity.RegistroGlicose;
import dev.com.br.gtechapp.entity.Usuario;
import dev.com.br.gtechapp.service.Util;
import dev.com.br.gtechapp.persistence.DadosFirebase;

import static dev.com.br.gtechapp.entity.Usuario.VAl_PARAM_HIPER_PADRAO;
import static dev.com.br.gtechapp.entity.Usuario.VAl_PARAM_HIPO_PADRAO;

public class DashboardFragment extends SimpleFragment {

    private DatabaseReference mDatabase;

    private PieChart mChart;

    public DashboardFragment(){

    }

    private List<RegistroGlicose> listaRegistrosFull;
    private List<ContagemRegistros> listaAnaliseRegistros1;
    private List<ContagemRegistros> listaAnaliseRegistros2;
    private List<ContagemRegistros> listaAnaliseHiper;
    private List<ContagemRegistros> listaAnaliseHipo;

    DadosFirebase dbFirebase;
    private DatabaseReference mUserReference;
    private FirebaseAuth mAuth;
    Usuario usuario;

    private TextInputEditText datInicio, datFim;
    private Button btn2semanas, btn1mes, btn3meses;
    private TextView tvwMediaDiaria, tvwQtdTotal;

    //Calendário
    private int mYear, mMonth, mDay;

    public static DashboardFragment newInstance() {
        DashboardFragment f = new DashboardFragment();
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        dbFirebase = new DadosFirebase();
        mAuth = FirebaseAuth.getInstance();
        mUserReference = FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getUid());

        definirObjetos(view);

        filtroPredefinido(30);

        initFirebaseReferences();

        return view;
    }

    private void filtroPredefinido(int qtdDiasInicio){
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, (-1) * qtdDiasInicio);
        String strDatInicio = Util.convertDatToString(c);

        c = Calendar.getInstance();
        String strDatFim =  Util.convertDatToString(c);

        datInicio.setText(Util.converteDataSqlParaBr(strDatInicio));
        datFim.setText(Util.converteDataSqlParaBr(strDatFim));
    }

    private void definirObjetos(View view){

        datInicio = view.findViewById(R.id.datInicio);
        datInicio.setFocusable(false);
        datInicio.setInputType(0);
        obterDatHoraAgora(datInicio);
        datInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDatePicker(datInicio);
            }
        });

        datFim = view.findViewById(R.id.datFim);
        datFim.setFocusable(false);
        datFim.setInputType(0);
        obterDatHoraAgora(datFim);
        datFim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDatePicker(datFim);
            }
        });

        btn2semanas = view.findViewById(R.id.btn2semanas);
        btn2semanas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filtroPredefinido(14);
                initFirebaseReferences();
            }
        });

        btn1mes = view.findViewById(R.id.btn1mes);
        btn1mes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filtroPredefinido(30);
                initFirebaseReferences();
            }
        });

        btn3meses = view.findViewById(R.id.btn3meses);
        btn3meses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filtroPredefinido(90);
                initFirebaseReferences();
            }
        });

        tvwQtdTotal = view.findViewById(R.id.tvwQtdTotal);
        tvwMediaDiaria = view.findViewById(R.id.tvwMediaDiaria);
    }

    private void mostrarDatePicker(final TextInputEditText v){

        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), AlertDialog.THEME_HOLO_DARK,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String strMes;
                        String strDia;

                        int intMes = monthOfYear + 1;

                        if (intMes < 10)
                        {
                            strMes = "0" + (intMes);
                        }else {
                            strMes = (intMes) + "";
                        }

                        if (dayOfMonth < 10 ){
                            strDia = "0" + dayOfMonth;
                        } else{
                            strDia = dayOfMonth + "";
                        }

                        String dat = year + "-"  + strMes + "-" + strDia;

                        if(v.getId() == R.id.datFim){

                            if(Util.convertStringToDate(dat).before(Util.convertStringToDate(Util.converteDataBrParaSql(datInicio.getText().toString())))){
                                v.setText(datInicio.getText().toString());
                            }else{
                                v.setText(Util.converteDataSqlParaBr(dat));
                            }

                        } else{
                            if(Util.convertStringToDate(dat).after(Util.convertStringToDate(Util.converteDataBrParaSql(datFim.getText().toString())))){
                                v.setText(datFim.getText().toString());
                            } else{
                                v.setText(Util.converteDataSqlParaBr(dat));
                            }
                        }

                        initFirebaseReferences();
                    }
                }, mYear, mMonth - 1, mDay); //month de 0 a 11
        datePickerDialog.show();
    }

    private void obterDatHoraAgora(TextInputEditText view){
        // Get Current Date
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH) + 1;
        mDay = c.get(Calendar.DAY_OF_MONTH);

        String strMes;
        String strDia;

        if (mMonth < 10)
        {
            strMes = "0" + (mMonth);
        }else {
            strMes = (mMonth) + "";
        }

        if (mDay < 10 ){
            strDia = "0" + mDay;
        } else{
            strDia = mDay + "";
        }

        view.setText(Util.converteDataSqlParaBr(mYear + "-" + strMes + "-" + strDia));
    }

    public void initFirebaseReferences(){

        String strDatIni = Util.converteDataBrParaSql(datInicio.getText().toString());
        String strDatFim = Util.converteDataBrParaSql(datFim.getText().toString());

        Query dbFirebaseQuery = FirebaseDatabase.getInstance().getReference()
                .child("user-registros-glicose")
                .child(mAuth.getUid())
                .orderByChild("datReg")
                .startAt(strDatIni)
                .endAt(strDatFim);

        listaRegistrosFull = new ArrayList<>();


        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                usuario = dataSnapshot.getValue(Usuario.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(getClass().getName(), "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        mUserReference.addValueEventListener(userListener);

        ValueEventListener registroGlicoseListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                listaRegistrosFull = dbFirebase.obterListaRegistrosGlicose(dataSnapshot);

                definirListasDosGraficos(listaRegistrosFull);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(getClass().getName(), "loadRegistrosGlicose:onCancelled", databaseError.toException());
                // ...
            }
        };
        dbFirebaseQuery.addValueEventListener(registroGlicoseListener);
    }

    private void definirListasDosGraficos(List<RegistroGlicose> listaRegistrosFull){

        listaAnaliseRegistros1 = new ArrayList<>();
        listaAnaliseRegistros2 = new ArrayList<>();
        listaAnaliseHiper = new ArrayList<>();
        listaAnaliseHipo = new ArrayList<>();

        listaAnaliseRegistros1.add(new ContagemRegistros("Pré Refeição")); //indice 0
        listaAnaliseRegistros1.add(new ContagemRegistros("Pós Refeição"));
        listaAnaliseRegistros1.add(new ContagemRegistros("Jejum"));
        listaAnaliseRegistros1.add(new ContagemRegistros("Outro")); //indice 3

        listaAnaliseRegistros2.add(new ContagemRegistros("Hipoglicemia")); //indice 0
        listaAnaliseRegistros2.add(new ContagemRegistros("Hiperglicemia"));
        listaAnaliseRegistros2.add(new ContagemRegistros("Normal")); // indice 2

        listaAnaliseHipo.add(new ContagemRegistros("Pré Refeição")); //indice 0
        listaAnaliseHipo.add(new ContagemRegistros("Pós Refeição"));
        listaAnaliseHipo.add(new ContagemRegistros("Jejum"));
        listaAnaliseHipo.add(new ContagemRegistros("Outro")); //indice 3

        listaAnaliseHiper.add(new ContagemRegistros("Pré Refeição")); //indice 0
        listaAnaliseHiper.add(new ContagemRegistros("Pós Refeição"));
        listaAnaliseHiper.add(new ContagemRegistros("Jejum"));
        listaAnaliseHiper.add(new ContagemRegistros("Outro")); //indice 3


        //Variáveis das tabelas
        ArrayList<Integer> listaValoresTotal = new ArrayList<>();
        ArrayList<Integer> listaValoresPre = new ArrayList<>();
        ArrayList<Integer> listaValoresPos = new ArrayList<>();
        ArrayList<Integer> listaValoresJejum = new ArrayList<>();

        ArrayList<Integer> listaValoresDom = new ArrayList<>();
        ArrayList<Integer> listaValoresSeg = new ArrayList<>();
        ArrayList<Integer> listaValoresTer = new ArrayList<>();
        ArrayList<Integer> listaValoresQua = new ArrayList<>();
        ArrayList<Integer> listaValoresQui = new ArrayList<>();
        ArrayList<Integer> listaValoresSex = new ArrayList<>();
        ArrayList<Integer> listaValoresSab = new ArrayList<>();


        for(int i = 0; i < listaRegistrosFull.size(); i++) {

            listaValoresTotal.add(listaRegistrosFull.get(i).getValRegistro());

            if (listaRegistrosFull.get(i).getNomEstadoAlimentacao() != null && listaRegistrosFull.get(i).getNomEstadoAlimentacao().equals("Pré Refeição")) {
                listaAnaliseRegistros1.get(0).addOne();
                listaValoresPre.add(listaRegistrosFull.get(i).getValRegistro());

                if(listaRegistrosFull.get(i).getValRegistro() < obterParamHipo()){
                    listaAnaliseRegistros2.get(0).addOne();
                    listaAnaliseHipo.get(0).addOne();
                } else if(listaRegistrosFull.get(i).getValRegistro() > obterParamHiper()){
                    listaAnaliseRegistros2.get(1).addOne();
                    listaAnaliseHiper.get(0).addOne();
                } else {
                    listaAnaliseRegistros2.get(2).addOne();
                }

            } else if (listaRegistrosFull.get(i).getNomEstadoAlimentacao() != null && listaRegistrosFull.get(i).getNomEstadoAlimentacao().equals("Pós Refeição")) {
                listaAnaliseRegistros1.get(1).addOne();
                listaValoresPos.add(listaRegistrosFull.get(i).getValRegistro());

                if(listaRegistrosFull.get(i).getValRegistro() < obterParamHipo()){
                    listaAnaliseRegistros2.get(0).addOne();
                    listaAnaliseHipo.get(1).addOne();
                } else if(listaRegistrosFull.get(i).getValRegistro() > obterParamHiper()){
                    listaAnaliseRegistros2.get(1).addOne();
                    listaAnaliseHiper.get(1).addOne();
                } else {
                    listaAnaliseRegistros2.get(2).addOne();
                }

            } else if (listaRegistrosFull.get(i).getNomEstadoAlimentacao() != null && listaRegistrosFull.get(i).getNomEstadoAlimentacao().equals("Jejum")) {
                listaAnaliseRegistros1.get(2).addOne();
                listaValoresJejum.add(listaRegistrosFull.get(i).getValRegistro());

                if(listaRegistrosFull.get(i).getValRegistro() < obterParamHipo()){
                    listaAnaliseRegistros2.get(0).addOne();
                    listaAnaliseHipo.get(2).addOne();
                } else if(listaRegistrosFull.get(i).getValRegistro() > obterParamHiper()){
                    listaAnaliseRegistros2.get(1).addOne();
                    listaAnaliseHiper.get(2).addOne();
                } else {
                    listaAnaliseRegistros2.get(2).addOne();
                }

            } else {
                listaAnaliseRegistros1.get(3).addOne();

                if(listaRegistrosFull.get(i).getValRegistro() < obterParamHipo()){
                    listaAnaliseRegistros2.get(0).addOne();
                    listaAnaliseHipo.get(3).addOne();
                } else if(listaRegistrosFull.get(i).getValRegistro() > obterParamHiper()){
                    listaAnaliseRegistros2.get(1).addOne();
                    listaAnaliseHiper.get(3).addOne();
                } else {
                    listaAnaliseRegistros2.get(2).addOne();
                }
            }

            if(Util.obterDiaDaSemana(listaRegistrosFull.get(i).getDatReg()) == 1){
                listaValoresDom.add(listaRegistrosFull.get(i).getValRegistro());
            }else if (Util.obterDiaDaSemana(listaRegistrosFull.get(i).getDatReg()) == 2){
                listaValoresSeg.add(listaRegistrosFull.get(i).getValRegistro());
            }else if(Util.obterDiaDaSemana(listaRegistrosFull.get(i).getDatReg()) == 3){
                listaValoresTer.add(listaRegistrosFull.get(i).getValRegistro());
            }else if (Util.obterDiaDaSemana(listaRegistrosFull.get(i).getDatReg()) == 4){
                listaValoresQua.add(listaRegistrosFull.get(i).getValRegistro());
            }else if (Util.obterDiaDaSemana(listaRegistrosFull.get(i).getDatReg()) == 5){
                listaValoresQui.add(listaRegistrosFull.get(i).getValRegistro());
            }else if (Util.obterDiaDaSemana(listaRegistrosFull.get(i).getDatReg()) == 6){
                listaValoresSex.add(listaRegistrosFull.get(i).getValRegistro());
            }else if (Util.obterDiaDaSemana(listaRegistrosFull.get(i).getDatReg()) == 7){
                listaValoresSab.add(listaRegistrosFull.get(i).getValRegistro());
            }
        }

        if(getView() != null){

            tvwQtdTotal.setText(String.valueOf(listaRegistrosFull.size()));

            try{
                SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");

                if(datInicio.getText() != null && datFim.getText() != null ){
                    String datI = datInicio.getText().toString();
                    String datF = datFim.getText().toString();

                    Date dat1 = format.parse(datI);
                    Date dat2 = format.parse(datF);

                    Calendar c1 = Calendar.getInstance();
                    Calendar c2 = Calendar.getInstance();

                    c1.setTime(dat1);
                    c2.setTime(dat2);

                    long qtdDias = Util.daysBetween(c1, c2) + 1;

                    double d = ((double) listaRegistrosFull.size() / (double)qtdDias);
                    DecimalFormat numberFormat = new DecimalFormat("###.##");
                    tvwMediaDiaria.setText(numberFormat.format(d));
                }
            }catch (Exception e){
                Toast.makeText(getActivity(), "Erro ao tentar converter datas", Toast.LENGTH_LONG).show();
            }

            gerarGrafico(getView(), listaAnaliseRegistros1, "Análise de Registros,\nAlimentação", getView().findViewById(R.id.grpAnaliseRegistro1).getId());
            gerarGrafico(getView(), listaAnaliseRegistros2, "Análise de Registros,\nCondição", getView().findViewById(R.id.grpAnaliseRegistro2).getId());
            gerarGrafico(getView(), listaAnaliseHiper, "Análise de Hiperglicemia", getView().findViewById(R.id.grpAnaliseHiper).getId());
            gerarGrafico(getView(), listaAnaliseHipo, "Análise de Hipoglicemia", getView().findViewById(R.id.grpAnaliseHipo).getId());

            gerarTabela1(listaValoresTotal, listaValoresPre, listaValoresPos, listaValoresJejum);
            gerarTabela2(listaValoresDom, listaValoresSeg, listaValoresTer, listaValoresQua, listaValoresQui, listaValoresSex, listaValoresSab);
        }

    }

    private double obterParamHipo(){
        if(usuario != null && usuario.getValHipo()>= 0) {
            return usuario.getValHipo();
        }else{
            return VAl_PARAM_HIPO_PADRAO;
        }
    }

    private double obterParamHiper(){
        if(usuario != null && usuario.getValHiper()>= 0) {
            return usuario.getValHiper();
        }else{
            return VAl_PARAM_HIPER_PADRAO;
        }
    }

    private void gerarGrafico(View view, List<ContagemRegistros> listaDadosGrafico, String nomTitulo, int idGrafico){

        PieChart mChart = view.findViewById(idGrafico);
        mChart.setUsePercentValues(true);
        mChart.getDescription().setEnabled(false);
        mChart.setExtraOffsets(5, 10, 5, 5);

        mChart.setDragDecelerationFrictionCoef(0.95f);

        Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), "OpenSans-Light.ttf");

        mChart.setCenterTextTypeface(tf);
        mChart.setCenterText(generateCenterText(nomTitulo));
        mChart.setCenterTextSize(10f);

        mChart.setExtraOffsets(20.f, 0.f, 20.f, 0.f);

        mChart.setDrawHoleEnabled(true);
        mChart.setHoleColor(Color.WHITE);

        mChart.setTransparentCircleColor(Color.WHITE);
        mChart.setTransparentCircleAlpha(110);

        mChart.setEntryLabelColor(Color.TRANSPARENT);
        mChart.setEntryLabelTextSize(8f);

        // radius of the center hole in percent of maximum radius
        mChart.setHoleRadius(58f);
        mChart.setTransparentCircleRadius(61f);

        mChart.setDrawCenterText(true);

        mChart.setRotationAngle(0);
        // enable rotation of the chart by touch
        mChart.setRotationEnabled(true);
        mChart.setHighlightPerTapEnabled(true);

        Legend legend = mChart.getLegend();
//        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.CENTER);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
//        legend.setOrientation(Legend.LegendOrientation.VERTICAL);
        legend.setDrawInside(false);
//
//        Legend l = mChart.getLegend();
//        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
//        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
//        l.setOrientation(Legend.LegendOrientation.VERTICAL);
//        l.setDrawInside(false);

        mChart.setData(generatePieData(listaDadosGrafico, nomTitulo));

        mChart.highlightValues(null);

        mChart.invalidate();

    }

    private void gerarTabela1(ArrayList<Integer> total, ArrayList<Integer> pre, ArrayList<Integer> pos, ArrayList<Integer> jejum){
        ((TextView)getView().findViewById(R.id.cel_media_total)).setText(String.valueOf(average(total)));
        ((TextView)getView().findViewById(R.id.cel_media_pre)).setText(String.valueOf(average(pre)));
        ((TextView)getView().findViewById(R.id.cel_media_pos)).setText(String.valueOf(average(pos)));
        ((TextView)getView().findViewById(R.id.cel_media_jejum)).setText(String.valueOf(average(jejum)));

        ((TextView)getView().findViewById(R.id.cel_desvio_total)).setText(String.valueOf(standardDeviation(total, 1)));
        ((TextView)getView().findViewById(R.id.cel_desvio_pre)).setText(String.valueOf(standardDeviation(pre, 1)));
        ((TextView)getView().findViewById(R.id.cel_desvio_pos)).setText(String.valueOf(standardDeviation(pos, 1)));
        ((TextView)getView().findViewById(R.id.cel_desvio_jejum)).setText(String.valueOf(standardDeviation(jejum, 1)));

        ((TextView)getView().findViewById(R.id.cel_maximo_total)).setText(String.valueOf(max(total)));
        ((TextView)getView().findViewById(R.id.cel_maximo_pre)).setText(String.valueOf(max(pre)));
        ((TextView)getView().findViewById(R.id.cel_maximo_pos)).setText(String.valueOf(max(pos)));
        ((TextView)getView().findViewById(R.id.cel_maximo_jejum)).setText(String.valueOf(max(jejum)));

        ((TextView)getView().findViewById(R.id.cel_minimo_total)).setText(String.valueOf(min(total)));
        ((TextView)getView().findViewById(R.id.cel_minimo_pre)).setText(String.valueOf(min(pre)));
        ((TextView)getView().findViewById(R.id.cel_minimo_pos)).setText(String.valueOf(min(pos)));
        ((TextView)getView().findViewById(R.id.cel_minimo_jejum)).setText(String.valueOf(min(jejum)));
    }

    private void gerarTabela2(ArrayList<Integer> dom, ArrayList<Integer> seg, ArrayList<Integer> ter, ArrayList<Integer> qua,
                              ArrayList<Integer> qui, ArrayList<Integer> sex, ArrayList<Integer> sab){

        ((TextView)getView().findViewById(R.id.cel_media_domingo)).setText(String.valueOf(average(dom)));
        ((TextView)getView().findViewById(R.id.cel_media_segunda)).setText(String.valueOf(average(seg)));
        ((TextView)getView().findViewById(R.id.cel_media_terca)).setText(String.valueOf(average(ter)));
        ((TextView)getView().findViewById(R.id.cel_media_quarta)).setText(String.valueOf(average(qua)));
        ((TextView)getView().findViewById(R.id.cel_media_quinta)).setText(String.valueOf(average(qui)));
        ((TextView)getView().findViewById(R.id.cel_media_sexta)).setText(String.valueOf(average(sex)));
        ((TextView)getView().findViewById(R.id.cel_media_sabado)).setText(String.valueOf(average(sab)));

        ((TextView)getView().findViewById(R.id.cel_desvio_domingo)).setText(String.valueOf(standardDeviation(dom, 1)));
        ((TextView)getView().findViewById(R.id.cel_desvio_segunda)).setText(String.valueOf(standardDeviation(seg, 1)));
        ((TextView)getView().findViewById(R.id.cel_desvio_terca)).setText(String.valueOf(standardDeviation(ter, 1)));
        ((TextView)getView().findViewById(R.id.cel_desvio_quarta)).setText(String.valueOf(standardDeviation(qua, 1)));
        ((TextView)getView().findViewById(R.id.cel_desvio_quinta)).setText(String.valueOf(standardDeviation(qui, 1)));
        ((TextView)getView().findViewById(R.id.cel_desvio_sexta)).setText(String.valueOf(standardDeviation(sex, 1)));
        ((TextView)getView().findViewById(R.id.cel_desvio_sabado)).setText(String.valueOf(standardDeviation(sab, 1)));

        ((TextView)getView().findViewById(R.id.cel_maximo_domingo)).setText(String.valueOf(max(dom)));
        ((TextView)getView().findViewById(R.id.cel_maximo_segunda)).setText(String.valueOf(max(seg)));
        ((TextView)getView().findViewById(R.id.cel_maximo_terca)).setText(String.valueOf(max(ter)));
        ((TextView)getView().findViewById(R.id.cel_maximo_quarta)).setText(String.valueOf(max(qua)));
        ((TextView)getView().findViewById(R.id.cel_maximo_quinta)).setText(String.valueOf(max(qui)));
        ((TextView)getView().findViewById(R.id.cel_maximo_sexta)).setText(String.valueOf(max(sex)));
        ((TextView)getView().findViewById(R.id.cel_maximo_sabado)).setText(String.valueOf(max(sab)));

        ((TextView)getView().findViewById(R.id.cel_minimo_domingo)).setText(String.valueOf(min(dom)));
        ((TextView)getView().findViewById(R.id.cel_minimo_segunda)).setText(String.valueOf(min(seg)));
        ((TextView)getView().findViewById(R.id.cel_minimo_terca)).setText(String.valueOf(min(ter)));
        ((TextView)getView().findViewById(R.id.cel_minimo_quarta)).setText(String.valueOf(min(qua)));
        ((TextView)getView().findViewById(R.id.cel_minimo_quinta)).setText(String.valueOf(min(qui)));
        ((TextView)getView().findViewById(R.id.cel_minimo_sexta)).setText(String.valueOf(min(sex)));
        ((TextView)getView().findViewById(R.id.cel_minimo_sabado)).setText(String.valueOf(min(sab)));

    }

    private SpannableString generateCenterText(String nomTitulo) {
        SpannableString s = new SpannableString(nomTitulo);
        s.setSpan(new RelativeSizeSpan(1.4f), 0, nomTitulo.length(), 0);
        s.setSpan(new StyleSpan(Typeface.ITALIC), 0, s.length(), 0);
        s.setSpan(new ForegroundColorSpan(ColorTemplate.getHoloBlue()), 0, s.length(), 0);

        return s;
    }

    public static int average(ArrayList<Integer> array) {
        float sum = 0.0f;
        for (int i = 0; i < array.size(); i++) {
            sum += ((Integer) array.get(i)).floatValue();
        }
        return (int) (sum / ((float) array.size()));
    }

    public static int standardDeviation(ArrayList<Integer> array, int option) {
        if (array.size() < 2) {
            return 0;
        }
        double sum = 0.0f;
        double meanValue = (double) average(array);
        for (int i = 0; i < array.size(); i++) {
            double diff = ((Integer) array.get(i)).floatValue() - meanValue;
            sum += diff * diff;
        }
        return (int) ((double) Math.sqrt((double) (sum / ((double) (array.size() - option)))));
    }

    public static int max(ArrayList<Integer> array) {
        float m = 0.0f;
        for (int i = 0; i < array.size(); i++) {
            if (m < ((Integer) array.get(i)).floatValue()) {
                m = ((Integer) array.get(i)).floatValue();
            }
        }
        return (int) m;
    }

    public static int min(ArrayList<Integer> array) {
        float m = 0.0f;
        for (int i = 0; i < array.size(); i++) {
            if (i == 0) {
                m = ((Integer) array.get(i)).floatValue();
            } else if (m > ((Integer) array.get(i)).floatValue()) {
                m = ((Integer) array.get(i)).floatValue();
            }
        }
        return (int) m;
    }
}

