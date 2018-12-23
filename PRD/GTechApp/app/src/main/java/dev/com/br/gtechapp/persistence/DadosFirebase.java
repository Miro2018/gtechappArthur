package dev.com.br.gtechapp.persistence;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dev.com.br.gtechapp.entity.RegistroGlicose;
import dev.com.br.gtechapp.entity.RegistroInsulina;
import dev.com.br.gtechapp.entity.RegistroNota;
import dev.com.br.gtechapp.entity.Usuario;

public class DadosFirebase {

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    public DadosFirebase(){
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
    }

    public boolean novoRegistroGlicose(RegistroGlicose rGlicose){
        try{

            String key = mDatabase.child("registros-glicose").push().getKey();
            Map<String, Object> postValues = rGlicose.toMap();
            Map<String, Object> childUpdates = new HashMap<>();
//            childUpdates.put("/registros-glicose/" + key, postValues);
            childUpdates.put("/user-registros-glicose/" + mAuth.getUid() + "/" + key, postValues);

            mDatabase.updateChildren(childUpdates);

            return true;
        }catch (Exception e){
            return false;
        }
    }

    public boolean atualizaRegistroGlicose(RegistroGlicose rGlicose, String key){
        try{
            Map<String, Object> postValues = rGlicose.toMap();
            Map<String, Object> childUpdates = new HashMap<>();
//            childUpdates.put("/registros-glicose/" + key, postValues);
            childUpdates.put("/user-registros-glicose/" + mAuth.getUid() + "/" + key, postValues);

            mDatabase.updateChildren(childUpdates);

            return true;
        }catch (Exception e){
            return false;
        }
    }

    public String novoRegistroNota(RegistroNota rNota){
        try{

            String key = mDatabase.child("registros-nota").push().getKey();
            Map<String, Object> postValues = rNota.toMap();
            Map<String, Object> childUpdates = new HashMap<>();
//            childUpdates.put("/registros-nota/" + key, postValues);
            childUpdates.put("/user-registros-nota/" + mAuth.getUid() + "/" + key, postValues);

            mDatabase.updateChildren(childUpdates);

            return key;
        }catch (Exception e){
            return null;
        }
    }

    public boolean atualizaRegistroNota(RegistroNota rNota, String key){
        try{
            Map<String, Object> postValues = rNota.toMap();
            Map<String, Object> childUpdates = new HashMap<>();
//            childUpdates.put("/registros-nota/" + key, postValues);
            childUpdates.put("/user-registros-nota/" + mAuth.getUid() + "/" + key, postValues);

            mDatabase.updateChildren(childUpdates);

            return true;
        }catch (Exception e){
            return false;
        }
    }

    public List<RegistroNota> obterListaRegistrosNota(DataSnapshot snapshot){

        List<RegistroNota> listaRegistrosNota = new ArrayList<>();

        for (DataSnapshot glicoseSnapshot: snapshot.getChildren()) {
            RegistroNota rNota = glicoseSnapshot.getValue(RegistroNota.class);
            if(rNota.getDatReg() != null)
                listaRegistrosNota.add(rNota);
            rNota.setIdRegistro(glicoseSnapshot.getKey().toString());
        }

        return listaRegistrosNota;
    }

    public boolean novosRegistroGlicose(List<RegistroGlicose> listaRegGlicose){
        try{
            Map<String, Object> childUpdates = new HashMap<>();
            for (RegistroGlicose r : listaRegGlicose){
                String key = mDatabase.child("registros-glicose").push().getKey();
//                childUpdates.put("/registros-glicose/" + key, r.toMap());
                childUpdates.put("/user-registros-glicose/" + mAuth.getUid() + "/" + key, r.toMap());
            }

            mDatabase.updateChildren(childUpdates);

            return true;
        }catch (Exception e){
            return false;
        }
    }

    public List<RegistroGlicose> obterListaRegistrosGlicose(DataSnapshot snapshot){

        List<RegistroGlicose> listaRegistrosGlicose = new ArrayList<>();

        for (DataSnapshot glicoseSnapshot: snapshot.getChildren()) {
            RegistroGlicose rGlicose = glicoseSnapshot.getValue(RegistroGlicose.class);
            if(rGlicose.getDatReg() != null)
                listaRegistrosGlicose.add(rGlicose);
                rGlicose.setIdRegistro(glicoseSnapshot.getKey().toString());
        }

        return listaRegistrosGlicose;
    }

    public boolean novoRegistroInsulina(RegistroInsulina rInsulina){
        try{

            String key = mDatabase.child("registros-insulina").push().getKey();
            Map<String, Object> postValues = rInsulina.toMap();
            Map<String, Object> childUpdates = new HashMap<>();
//            childUpdates.put("/registros-insulina/" + key, postValues);
            childUpdates.put("/user-registros-insulina/" + mAuth.getUid() + "/" + key, postValues);

            mDatabase.updateChildren(childUpdates);

            return true;
        }catch (Exception e){
            return false;
        }
    }

    public boolean atualizaRegistroInsulina(RegistroInsulina rInsulina, String key){
        try{
            Map<String, Object> postValues = rInsulina.toMap();
            Map<String, Object> childUpdates = new HashMap<>();
//            childUpdates.put("/registros-insulina/" + key, postValues);
            childUpdates.put("/user-registros-insulina/" + mAuth.getUid() + "/" + key, postValues);

            mDatabase.updateChildren(childUpdates);

            return true;
        }catch (Exception e){
            return false;
        }
    }

    public List<RegistroInsulina> obterListaRegistrosInsulina(DataSnapshot snapshot){

        List<RegistroInsulina> listaRegistrosInsulina = new ArrayList<>();

        for (DataSnapshot insulinaSnapshot: snapshot.getChildren()) {
            RegistroInsulina rInsulina = insulinaSnapshot.getValue(RegistroInsulina.class);
            if(rInsulina.getDatReg() != null)
                listaRegistrosInsulina.add(rInsulina);
            rInsulina.setIdRegistro(insulinaSnapshot.getKey().toString());
        }

        return listaRegistrosInsulina;
    }

    public static void atualizaDatUltimoRegistroGlicose(String strDatUltimoRegistro, FirebaseAuth mAuth){
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("user-registros-glicose").child(mAuth.getUid()).child("0-ultimo-registro").child("datUltimoRegistro").setValue(strDatUltimoRegistro);
    }
}
