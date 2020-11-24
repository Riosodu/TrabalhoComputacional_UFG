package com.ufgec.trabalhocomputacional.model;

import com.google.gson.Gson;
import com.ufgec.trabalhocomputacional.classes.TipoVoo;
import com.ufgec.trabalhocomputacional.utils.Haversine;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Esta classe representa os destinos possíveis de qualquer voo.
 * Nela está armazenada todos os aeroportos que já foram criados e também
 * todos os voos que pousam ou decolam de um aeroporto.
 */
@Entity
@Table(name="TB_AEROPORTO")
public class Aeroporto {

    @Id
    private String nome;

    private String sigla;

    private String cidade;

    private String estado;

    private double latitude;

    private double longitude;

    /**
     * Nesta variável estão armazenados todos os aeroportos válidos(criados)
     */
    public static final Map<String, Aeroporto> AEROPORTOS = new HashMap<>();
    /**
     * Nesta variável estão armazenadas todas as decolagens de um aeroporto
     */
    private NavigableSet<Voo> voosDaqui = new TreeSet<>();
    /**
     * Nesta variável estão armazenados todos os pousos em um aeroporto
     */
    private NavigableSet<Voo> voosParaAqui = new TreeSet<>();

    private Aeroporto(String nome, String sigla, String cidade, String estado,
                     double latitude, double longitude) {
        this.nome = nome;
        this.sigla = sigla;
        this.cidade = cidade;
        this.estado = estado;
        this.latitude = latitude;
        this.longitude = longitude;

        try { //Checa se já não existe armazenado um objeto como este, caso não, adiciona ao armazenamento
            if (AEROPORTOS.containsKey(this.sigla))
                throw new IllegalArgumentException("Aeroporto com esta sigla já existe!");
            else
                AEROPORTOS.put(sigla, this);
        } catch(IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    public String getNome() {
        return nome;
    }

    public String getSigla() {
        return sigla;
    }

    public String getCidade() {
        return cidade;
    }

    public String getEstado() {
        return estado;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    /**
     * Esta classe serve para fins de desenvolvimento.
     * A partir dessa classe é possível gerar um novo Aeroporto
     * usando um arquivo JSON
     */
    public static Aeroporto novoAeroportoViaJSON(String json) {
        Aeroporto aeroporto = new Gson().fromJson(String.valueOf(json), Aeroporto.class);
        AEROPORTOS.put(aeroporto.sigla, aeroporto);
        aeroporto.voosDaqui = new TreeSet<>();
        aeroporto.voosParaAqui = new TreeSet<>();

        return aeroporto;
    }

    /**
     * Esta classe serve para fins de desenvolvimento.
     * A partir dessa classe é possível gerar vários novos Aeroporto
     * usando um arquivo JSON
     */
    public static Aeroporto[] novosAeroportosViaJSON(String json) {
        Aeroporto[] aeroportos = new Gson().fromJson(String.valueOf(json), Aeroporto[].class);
        for(Aeroporto aeroporto: aeroportos) {
            AEROPORTOS.put(aeroporto.sigla, aeroporto);
            aeroporto.voosDaqui = new TreeSet<>();
            aeroporto.voosParaAqui = new TreeSet<>();
        }

        return aeroportos;
    }

    /**
     * Este método calcula a distância entre dois aeroportos a partir
     * da fórmula de Haversine
     * @param outroAeroporto é o outro aeroporto que será medida a distância
     * @return distância entre dois aeroportos;
     */
    public double distancia(Aeroporto outroAeroporto) {
        return Haversine.distance(this.latitude, this.longitude, outroAeroporto.latitude, outroAeroporto.longitude);
    }

    /**
     * Retorna todas as decolagens deste aeroporto em um determinado intervalo,
     * que possuam assentos reservados para a classe selecionada
     * @param tipoVoo é a tipo da classe da passagem/voo
     * @param dataInicial é a data que delimitará o inicio do intervalo de busca
     * @param dataFinal é a data que delimitará o fim do intervalo de busca
     */
    public ArrayList<Voo> voosDaquiNumIntervalo(TipoVoo tipoVoo, LocalDate dataInicial, LocalDate dataFinal) {
        Voo[] voos = voosLocalDate(tipoVoo, dataInicial, dataFinal);

        Voo elementoInicial = voosDaqui.ceiling(voos[0]);
        Voo elementoFinal = voosDaqui.floor(voos[1]);

        TipoVoo[] voosDoTipo = tipoVoo.getClassesDisponiveis();
        ArrayList<Voo> listaVoos = new ArrayList<>();
        SortedSet<Voo> voosClasse;

        for(TipoVoo classe: voosDoTipo) {
            voosClasse = voosDaqui.subSet(elementoInicial, elementoFinal);
            listaVoos.addAll(voosClasse);
            listaVoos.sort(null);
        }

        return listaVoos;
    }

    /**
     * Retorna todos os pousos deste aeroporto em um determinado intervalo,
     * que possuam assentos reservados para a classe selecionada
     * @param tipoVoo é a tipo da classe da passagem/voo
     * @param dataInicial é a data que delimitará o inicio do intervalo de busca
     * @param dataFinal é a data que delimitará o fim do intervalo de busca
     */
    public ArrayList<Voo> voosParaAquiNumIntervalo(TipoVoo tipoVoo, LocalDate dataInicial, LocalDate dataFinal) {
        Voo[] voos = voosLocalDate(tipoVoo, dataInicial, dataFinal);

        Voo elementoInicial = voosParaAqui.ceiling(voos[0]);
        Voo elementoFinal = voosParaAqui.floor(voos[1]);

        TipoVoo[] voosDoTipo = tipoVoo.getClassesDisponiveis();
        ArrayList<Voo> listaVoos = new ArrayList<>();
        SortedSet<Voo> voosClasse;

        for(TipoVoo classe: voosDoTipo) {
            voosClasse = voosParaAqui.subSet(elementoInicial, elementoFinal);
            listaVoos.addAll(voosClasse);
            listaVoos.sort(null);
        }

        return listaVoos;
    }

    /**
     * Esta classe encapsula código em comum entre os métodos
     * voosDaquiNumIntervalo e voosParaAquiNumIntervalo()
     */
    private Voo[] voosLocalDate(TipoVoo tipoVoo, LocalDate dataInicial, LocalDate dataFinal) {
        Voo[] voos = new Voo[2];
        LocalDateTime inicial = LocalDateTime.of(dataInicial, LocalTime.of(0,0));
        LocalDateTime f_nal = LocalDateTime.of(dataFinal, LocalTime.of(23,59,59));

        voos[0] = new Voo(tipoVoo, inicial);
        voos[1] = new Voo(tipoVoo, f_nal);

        return voos;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Aeroporto)) return false;
        Aeroporto aeroporto = (Aeroporto) o;
        return Double.compare(aeroporto.latitude, latitude) == 0 &&
                Double.compare(aeroporto.longitude, longitude) == 0 &&
                nome.equals(aeroporto.nome) &&
                sigla.equals(aeroporto.sigla) &&
                cidade.equals(aeroporto.cidade) &&
                estado.equals(aeroporto.estado);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nome, sigla, cidade, estado, latitude, longitude);
    }

    @Override
    public String toString(){
        String toString = "";

        toString += "Nome: " + nome + "| Sigla: " + sigla;
        toString += "\nCidade: " + cidade;
        toString += "\nEstado: " + estado;

        return toString;
    }










    /**
     * Esta classe interna representa todos os voos que partem dos aeroportos.
     * A classe implementa a interface Comparable<E> para que os voos possam ser
     * organizados de forma adequada.
     */
    @Entity
    @Table(name="TB_VOO")
    public static class Voo implements Comparable<Voo>{

        @Id
        private Integer id;

        private Aeroporto localOrigem;

        private Aeroporto localDestino;

        private LocalDateTime horarioPartida;

        private LocalDateTime previsaoChegada;

        private TipoVoo tipoVoo;

        private Map<TipoVoo, SortedMap<Integer, Passageiro>> poltronas;
        private Map<TipoVoo, Integer> poltronasDisponiveis;

        /**
         * Este construtor serve exclusivamente para facilitar buscas de voos.
         * Os objetos criados a partir deste construtor não são contabilizados na contagem
         * de voos existentes.
         */
        private Voo(TipoVoo tipoVoo, LocalDateTime horarioPartida) {
            inicializador(tipoVoo, horarioPartida);
        }

        /**
         * Construtor padrão que inicia as variáveis da classe
         */
        public Voo(TipoVoo tipoVoo, Aeroporto localOrigem, Aeroporto localDestino, LocalDateTime horarioPartida) throws IllegalArgumentException {
            this.localOrigem = localOrigem;
            this.localDestino = localDestino;
            inicializador(tipoVoo, horarioPartida);


            try { //Checa se já não existe armazenado um objeto como este, caso não, adiciona ao armazenamento
                if(localOrigem.voosDaqui.contains(this))
                    throw new IllegalArgumentException("Um voo idêntico a este já existe!");
                else
                    localOrigem.voosDaqui.add(this);

                if(localDestino.voosParaAqui.contains(this))
                    throw new IllegalArgumentException("Um voo idêntico a este já existe!");
                else
                    localDestino.voosParaAqui.add(this);
            } catch(IllegalArgumentException e) {
                e.printStackTrace();
                throw e;
            }
        }

        /**
         * Este método encapsula código em comum entre os dois construtores.
         */
        private void inicializador(TipoVoo tipoVoo, LocalDateTime horarioPartida) {
            this.horarioPartida = horarioPartida;
            setPrevisaoChegada();

            this.tipoVoo = tipoVoo;
            TipoVoo[] classesDisponiveis = this.tipoVoo.getClassesDisponiveis();

            poltronas = new HashMap<>();
            poltronasDisponiveis = new HashMap<>();
            for (TipoVoo classe : classesDisponiveis) {//Inicializa o mapa das poltronas e a quantidade de poltronas disponíveis
                poltronas.put(classe, new TreeMap<>());
                poltronasDisponiveis.put(classe, classe.getQuantidadeAssentos());
            }
            id = hashCode();
        }


        public int getId() {
            return id;
        }

        public Aeroporto getLocalOrigem() {
            return localOrigem;
        }

        public Aeroporto getLocalDestino() {
            return localDestino;
        }

        public LocalDateTime getHorarioPartida() {
            return horarioPartida;
        }

        public LocalDateTime getPrevisaoChegada() {
            return previsaoChegada;
        }

        /**
         * Este método calcula o horário previsto de chegada do voo no seu destino
         */
        private void setPrevisaoChegada() {
            double distancia = localOrigem.distancia(localDestino);
            double razaoTempo = distancia / 1000; //A media da velocidade de voos comerciais é de 1000km/

            int horas, minutos;
            horas = (int) razaoTempo;
            minutos = (int) (((razaoTempo - horas)) * 60);

            previsaoChegada = horarioPartida.plusHours(horas).plusMinutes(minutos);
        }

        /**
         * Esta classe reserva uma poltrona para um passageiro na classe definida
         */
        public boolean reservarPoltrona(int poltrona, Passageiro passageiro, TipoVoo classe) {
            if(!this.possuiTipoVoo(classe))
                return false;
            else if (!possuiPoltronasDisponiveis(classe))
                return false;
            else if(!poltronaEstaDisponivel(poltrona, classe))
                return false;
            else {
                poltronas.get(classe).put(poltrona, passageiro);
                poltronasDisponiveis.replace(classe, poltronasDisponiveis.get(classe)-1);
                return true;
            }
        }

        /**
         * Este método verifica se um voo possui assentos reservados para uma classe.
         */
        private boolean possuiTipoVoo(TipoVoo tipoVoo) {
            return tipoVoo.ordinal() <= this.tipoVoo.ordinal();
        }

        /**
         * Verifica se uma poltrona de um tipo de classe ainda está disponível
         */
        public boolean poltronaEstaDisponivel(int poltrona, TipoVoo classe) {
            return !poltronas.get(classe).containsKey(poltrona);
        }

        /**
         * Verifica se uma classe de voo ainda possui poltronas não reservadas
         */
        public boolean possuiPoltronasDisponiveis(TipoVoo classe) {
           return poltronasDisponiveis.get(classe) > 0;
        }

        /**
         * Retorna o passageiro vinculado a uma poltrona de uma classe
         */
        public Passageiro getPassageiroPoltrona(int poltrona, TipoVoo classe) {
            if(poltronaEstaDisponivel(poltrona, classe))
                return null;
            else
                return poltronas.get(classe).get(poltrona);
        }

        /**
         * Este método compara dois voos para verificar qual acontecerá mais cedo. O desempate é definido através do id.
         */
        @Override
        public int compareTo(Voo o) {
            if(this.horarioPartida.isEqual(o.horarioPartida))
                if(this.id.equals(o.id))
                    return 0;
                else
                    return this.id - o.id;
            else
            if(horarioPartida.isAfter(o.horarioPartida))
                return Math.abs(this.id - o.id);
            else
                return -1 * Math.abs(this.id - o.id);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Voo)) return false;
            Voo voo = (Voo) o;
            return  localOrigem.equals(voo.localOrigem) &&
                    localDestino.equals(voo.localDestino) &&
                    horarioPartida.equals(voo.horarioPartida) &&
                    previsaoChegada.equals(voo.previsaoChegada) &&
                    tipoVoo.equals(voo.tipoVoo);
        }

        @Override
        public int hashCode() {
            return Objects.hash(localOrigem, localDestino, horarioPartida, previsaoChegada, tipoVoo);
        }

        @Override
        public String toString() {
            StringBuilder toString = new StringBuilder();

            DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("dd/MM/yy - HH:mm");

            toString.append("Voo de número:").append(Integer.toHexString(id));
            toString.append("\nLocal de origem: ").append(localOrigem.getNome());
            toString.append("\nLocal de destino: ").append(localDestino.getNome());
            toString.append("\nHorário de partida: ").append(horarioPartida.format(formatter1)).append("\t\t| Previsão de chegada: ").append(previsaoChegada.format(formatter1));

            toString.append("\nClasses: ");
            TipoVoo[] tiposVoo = tipoVoo.getClassesDisponiveis();
            for(int i = 0; i < tiposVoo.length; i++) {
                toString.append(tiposVoo[i].getNome());
                if(i == tiposVoo.length-1)
                    toString.append(".");
                else
                    toString.append(", ");
            }

            return toString.toString();
        }
    }
}
