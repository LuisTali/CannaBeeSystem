package Clases;

import UserRelated.IndoorConfig;

import java.io.*;
import java.security.Key;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class CannaBeeSystem {
    HashMapGen<Integer, HashMapGen<String, Cepa>> cepasUser = new HashMapGen<>();
    HashMapGen<Integer, Cepa> cepasBancos = new HashMapGen<>();
    HashMapGen<Integer, IndoorConfig> configsIndoor = new HashMapGen<>();
    Connect con = new Connect();

    public void agregarCepaUser(Integer id, Cepa c) {
        if (cepasUser.containsKey(id)) {
            cepasUser.elementByKey(id).añadir(c.getNombre(), c);
            System.out.println("ID ya creado: " + id);
        } else {
            HashMapGen<String, Cepa> registroCepas = new HashMapGen<>();
            registroCepas.añadir(c.getNombre(), c);
            System.out.println("ID Creado: " + id);
            cepasUser.añadir(id, registroCepas);
        }
    }

    public void agregarCepaBanco(Integer key, Cepa c) {
        cepasBancos.añadir(key, c);
    }

    public void agregarConfigIndoor(int idUser, String luz, String ventilador,  String indoor, String maceta, String cooler) {
        IndoorConfig iC = new IndoorConfig(idUser, luz, cooler, indoor, ventilador, maceta);
        configsIndoor.añadir(idUser, iC);
    }

    public void eliminarCepa(int id, String nombre) {
        if (cepasUser.containsKey(id)) {
            cepasUser.elementByKey(id).eliminar(nombre);
        }
    }

    public void editCepa(int id, String nombre, double thc, String raza, String comments) {
        Cepa aux = null;
        if (comments != null) aux = new Cepa(nombre, raza, thc, comments);
        else aux = new Cepa(nombre, raza, thc);
        HashMapGen<String, Cepa> auxH = cepasUser.elementByKey(id);
        if (auxH.containsKey(aux.getNombre()))
            cepasUser.elementByKey(id).elementByKey(nombre); //Finiquitar.
    }

    public boolean cepasListIsEmpty() {
        if (cepasUser.hSize() > 0) return false;
        else return true;
    }

    public boolean cepasUserIsEmpty(int id) {
        if (cepasUser.hSize() > 0)
            if (cepasUser.elementByKey(id).hSize() > 0) return false;
            else return true;
        else return false;
    }

    public Iterator getUserCepasListIterator() {
        return cepasUser.getIterator();
    }

    public Iterator getCepasUserIterator(int id) {
        return cepasUser.elementByKey(id).getIterator();
    }

    public Iterator getCepasBancosIterator() {
        return cepasBancos.getIterator();
    }

    public Iterator getBancosIterator() {
        return con.returnBanksName().iterator();
    }

    public Integer getStockGen(String nombre) {
        Integer auxStock = null;
        Iterator entries = getCepasBancosIterator();
        while (entries.hasNext()) {
            Map.Entry entry = (Map.Entry) entries.next();
            Integer key = (Integer) entry.getKey();
            Cepa c = (Cepa) entry.getValue();
            if (c.getNombre().equals(nombre)) auxStock = c.getStock();
        }
        return auxStock;
    }

    public void cepasToFile() {
        if (cepasUser.hSize() > 0) {
            try {
                File fl = new File("Data/GensUser.bin");
                createFolder(fl);
                FileOutputStream fo = new FileOutputStream(fl);
                ObjectOutputStream oO = new ObjectOutputStream(fo);
                System.out.println("Cargando archivo");
                System.out.println(cepasUser.mostrar());
                oO.writeObject(cepasUser);
                System.out.println("Mapa cargado");
                oO.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else System.out.println("Mapa Cepas Vacio"); //Borrar luego los sout del System.
    }

    public void configsToFile() {
        if (configsIndoor.hSize() > 0) {
            try {
                File fl = new File("Data/IndoorConfigs.bin");
                FileOutputStream fO = new FileOutputStream(fl);
                ObjectOutputStream oO = new ObjectOutputStream(fO);
                oO.writeObject(configsIndoor);
                oO.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void cepasReadFile() {
        try {
            File fl = new File("Data/GensUser.bin");
            if (fl.length() != 0) {
                FileInputStream fI = new FileInputStream(fl);
                ObjectInputStream oI = new ObjectInputStream(fI);
                int lectura = 1;
                while (lectura == 1) {
                    HashMapGen<Integer, HashMapGen<String, Cepa>> aux = (HashMapGen<Integer, HashMapGen<String, Cepa>>) oI.readObject();
                    System.out.println("Archivo siendo leido");
                    if (aux != null) {
                        cepasUser = aux;
                    }
                }
                oI.close();
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void configsReadFile() {
        try {
            File fl = new File("Data/IndoorConfigs.bin");
            FileInputStream fI = new FileInputStream(fl);
            ObjectInputStream oI = new ObjectInputStream(fI);
            int lectura = 1;
            while (lectura == 1){
                HashMapGen<Integer,IndoorConfig> auxH = (HashMapGen<Integer, IndoorConfig>) oI.readObject();
                configsIndoor = auxH;
            }
            oI.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public Iterator getCepasPorBancoIterator(String nombre) {
        return con.retornarGensPorBanco(nombre).getIterator();
    }

    public IndoorConfig getIndoorConfig(int id){
        return configsIndoor.elementByKey(id);
    }

    public void cepasBanksReadSQL() {
        cepasBancos = con.consultarCepasBancos();
    }

    public void createFolder(File file) {
        File folder = file.getParentFile();
        if (!folder.exists()) folder.mkdir();
    }
}
