package com.ufgec.trabalhocomputacional.model;

import com.google.gson.Gson;
import com.ufgec.trabalhocomputacional.classes.TipoVoo;
import com.ufgec.trabalhocomputacional.utils.Grafo;
import com.ufgec.trabalhocomputacional.utils.ConteudoAresta;
import com.ufgec.trabalhocomputacional.utils.Haversine;
import com.ufgec.trabalhocomputacional.utils.ListaAleatoriaPonderada;
import com.ufgec.trabalhocomputacional.utils.Uteis;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.File;
import java.io.IOException;
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
public class Aeroporto implements Comparable<Aeroporto>{

    @Id
    private final String nome;

    private final String sigla;

    private final String cidade;

    private final String estado;

    private final String siglaEstado;

    private int numeroPassageirosAno;

    private int numeroPassageirosAnoOriginal;

    private final double latitude;

    private final double longitude;



    /**
     * Nesta variável estão armazenados todos os aeroportos válidos(criados)
     */
    private static final SortedMap<String, Aeroporto> AEROPORTOS = new TreeMap<>();

    private static final Grafo<Aeroporto, Voo> VOOS = new Grafo<>(100);

    private static final Grafo<Aeroporto, Rota> ROTAS_DISTANCIA = new Grafo<>(100);
    private static final Grafo<Aeroporto, Rota> ROTAS_TEMPO = new Grafo<>(100);
    private static final Grafo<Aeroporto, Rota> ROTAS_PRECO = new Grafo<>(100);

    Aeroporto(String nome, String sigla, String cidade, String estado, String siglaEstado,
              int numeroPassageirosAno, double latitude, double longitude) {
        this.nome = nome;
        this.sigla = sigla;
        this.cidade = cidade;
        this.estado = estado;
        this.siglaEstado = siglaEstado;
        this.numeroPassageirosAno = numeroPassageirosAno;
        this.latitude = latitude;
        this.longitude = longitude;
        retificarNumeroPassageirosAno();


        try { //Checa se já não existe armazenado um objeto como este, caso não, adiciona ao armazenamento
            if (AEROPORTOS.containsKey(this.sigla))
                throw new IllegalArgumentException("Aeroporto com esta sigla já existe!");
            else
                AEROPORTOS.put(sigla, this);
        } catch(IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    private void retificarNumeroPassageirosAno() {
        if(numeroPassageirosAno<=37500) {
            numeroPassageirosAnoOriginal = numeroPassageirosAno;
        } else {
            numeroPassageirosAnoOriginal = numeroPassageirosAno;
            numeroPassageirosAno = (int) (50 * Math.sqrt(15 * numeroPassageirosAno));
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

    public String getSiglaEstado() {
        return siglaEstado;
    }

    public int getNumeroPassageirosAno() {
        return numeroPassageirosAno;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
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
     * Retorna todas as decolagens deste aeroporto em um determinado intervalo.
     * @param dataInicial é a data que delimitará o inicio do intervalo de busca
     * @param dataFinal é a data que delimitará o fim do intervalo de busca
     */
    public List<Voo> voosDaquiNumIntervalo(LocalDate dataInicial, LocalDate dataFinal) {
        List<Voo> voos = new ArrayList<>();
        boolean estaNoIntervalo;
        LocalDate tempDate;

        for(ConteudoAresta<Voo> conteudoAresta :  VOOS.getArestasDe(this)) {
            tempDate = conteudoAresta.getObjetoDado().horarioPartida.toLocalDate();
            estaNoIntervalo = (tempDate.isEqual(dataInicial) || tempDate.isAfter(dataInicial))
                    && (tempDate.isEqual(dataFinal) || tempDate.isBefore(dataFinal));
            if (estaNoIntervalo)
                voos.add(conteudoAresta.getObjetoDado());
        }
        return voos;
    }

    /**
     * Retorna todos os pousos deste aeroporto em um determinado intervalo.
     * @param dataInicial é a data que delimitará o inicio do intervalo de busca
     * @param dataFinal é a data que delimitará o fim do intervalo de busca
     */
    public List<Voo> voosParaAquiNumIntervalo(LocalDate dataInicial, LocalDate dataFinal) {
        List<Voo> voos = new ArrayList<>();
        boolean estaNoIntervalo;
        LocalDate tempDate;

        for(ConteudoAresta<Voo> conteudoAresta :  VOOS.getArestasPara(this)) {
            tempDate = conteudoAresta.getObjetoDado().horarioPartida.toLocalDate();
            estaNoIntervalo = (tempDate.isAfter(dataInicial) || tempDate.isEqual(dataInicial))
                    && (tempDate.isBefore(dataFinal) || tempDate.isEqual(dataFinal));
            if (estaNoIntervalo)
                voos.add(conteudoAresta.getObjetoDado());
        }
        return voos;
    }

    public List<Voo> voosParaAquiDe(Aeroporto origem) {
        List<Voo> voos = new ArrayList<>();
        for(ConteudoAresta<Voo> voo: VOOS.getArestasDePara(origem, this))
            voos.add(voo.getObjetoDado());
        return voos;
    }

    public List<Voo> voosDaquiPara(Aeroporto destino) {
        List<Voo> voos = new ArrayList<>();
        for(ConteudoAresta<Voo> voo: VOOS.getArestasDePara(this, destino))
            voos.add(voo.getObjetoDado());
        return voos;
    }

    public List<Voo> voosDaquiParaNumIntervalo(Aeroporto para, LocalDate dataInicial, LocalDate dataFinal) {
        List<Voo> voos = new ArrayList<>();
        LocalDate tempDate;
        boolean estaNoIntervalo;

        for(ConteudoAresta<Voo> conteudoAresta: VOOS.getArestasDePara(this, para)) {
            tempDate = conteudoAresta.getObjetoDado().horarioPartida.toLocalDate();
            estaNoIntervalo = (tempDate.isAfter(dataInicial) || tempDate.isEqual(dataInicial))
                    && (tempDate.isBefore(dataFinal) || tempDate.isEqual(dataFinal));
            if (estaNoIntervalo)
                voos.add(conteudoAresta.getObjetoDado());
        }

        return voos;
    }

    public static List<Deque<Aeroporto>> getPossiveisRotas(Aeroporto de, Aeroporto para, int maximoConexoes) {
        return VOOS.rotaViaDFS(de, para, maximoConexoes);
    }

    public int mediaVoosDiarios() {
        int passageirosPorDia = (numeroPassageirosAno / 365);
        return passageirosPorDia / TipoVoo.mediaPassageirosPorVoo();
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

    @Override
    public int compareTo(Aeroporto o) {
        return Comparator.comparing(Aeroporto::getNumeroPassageirosAno)
                .thenComparing(Aeroporto::getEstado)
                .thenComparing(Aeroporto::getCidade)
                .thenComparing(Aeroporto::getSigla)
                .compare(this, o);
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
        private Voo() {
        }

        /**
         * Construtor padrão que inicia as variáveis da classe
         */
        public Voo(TipoVoo tipoVoo, Aeroporto localOrigem, Aeroporto localDestino, LocalDateTime horarioPartida) throws IllegalArgumentException {
            this.localOrigem = localOrigem;
            this.localDestino = localDestino;
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

            try { //Checa se já não existe armazenado um objeto como este, caso não, adiciona ao armazenamento
                if(!VOOS.adicionarAresta(localOrigem, localDestino, this, localOrigem.distancia(localDestino)))
                    throw new IllegalArgumentException("Um voo idêntico a este já existe!");
            } catch(IllegalArgumentException e) {
                e.printStackTrace();
                throw e;
            }
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
            minutos = Uteis.arredondarParaProximo5(minutos);

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
            return Comparator.comparing(Voo::getHorarioPartida)
                    .thenComparing(Voo::getPrevisaoChegada)
                    .thenComparing(Voo::getId)
                    .compare(this, o);
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

            toString.append("Voo de número: #").append(Integer.toHexString(id).toUpperCase());

            toString.append("\nLocal de origem: ").append(localOrigem.cidade).append(" - ")
                    .append(localOrigem.siglaEstado).append(" | ")
                    .append(localOrigem.nome);


            toString.append("\nLocal de destino: ").append(localDestino.cidade).append(" - ")
                    .append(localDestino.siglaEstado).append(" | ")
                    .append(localDestino.nome);

            toString.append("\nHorário de partida: ")
                    .append(horarioPartida.format(formatter1)).append(" | Previsão de chegada: ")
                    .append(previsaoChegada.format(formatter1));

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


    /**
     * Esta classe é uma classe de utilidade que gera voos aleatoriamente, respeitando certos parâmetros.
     */
    public static class GeradorVoos {
        //--------------------------------------------------------------------------------------------------------------//
        //Este bloco define constantes de períodos do dia.
        private static final LocalTime[] MANHA_EARLY = {LocalTime.of(6, 0), LocalTime.of(7, 59)};
        private static final LocalTime[] MANHA_MID = {LocalTime.of(8, 0), LocalTime.of(9, 59)};
        private static final LocalTime[] MANHA_LATE = {LocalTime.of(10, 0), LocalTime.of(11, 59)};
        private static final LocalTime[] TARDE_EARLY = {LocalTime.of(12, 0), LocalTime.of(13, 59)};
        private static final LocalTime[] TARDE_MID = {LocalTime.of(14, 0), LocalTime.of(15, 59)};
        private static final LocalTime[] TARDE_LATE = {LocalTime.of(16, 0), LocalTime.of(17, 59)};
        private static final LocalTime[] NOITE_EARLY = {LocalTime.of(18, 0), LocalTime.of(19, 59)};
        private static final LocalTime[] NOITE_MID = {LocalTime.of(20, 0), LocalTime.of(21, 59)};
        private static final LocalTime[] NOITE_LATE = {LocalTime.of(22, 0), LocalTime.of(23, 59)};
        private static final LocalTime[] MADRUGADA = {LocalTime.of(0, 0), LocalTime.of(5, 59)};
        //--------------------------------------------------------------------------------------------------------------//

        public static List<Voo> gerarVoos(int dias) {
            //----------------------------------------------------------------------------------------------------------//
            /*Este bloco de código insere as constantes de períodos do dia em uma lista
             * que os retorna de forma aleatória levando em consideração o valor do peso atribuído.
             */
            ListaAleatoriaPonderada<LocalTime[]> probabilidadeHorario = new ListaAleatoriaPonderada<>();
            probabilidadeHorario.adicionar(MANHA_EARLY, 1);
            probabilidadeHorario.adicionar(MANHA_MID, 2.5); //(1.0, 3.5]
            probabilidadeHorario.adicionar(MANHA_LATE, 2); // (3.5, 5.5]
            probabilidadeHorario.adicionar(TARDE_EARLY, 1.5);//(5.5, 7.0]
            probabilidadeHorario.adicionar(TARDE_MID, 1.5);//(7.0, 8.5]
            probabilidadeHorario.adicionar(TARDE_LATE, 1);//(8.5, 9.5]
            probabilidadeHorario.adicionar(NOITE_EARLY, 1);//(9.5, 10.5]
            probabilidadeHorario.adicionar(NOITE_MID, 0.5);//(10.5, 11.0]
            probabilidadeHorario.adicionar(NOITE_LATE, 0.25);//(11.0, 11.25]
            probabilidadeHorario.adicionar(MADRUGADA, 0.5);//(11.25, 11.75]

            //----------------------------------------------------------------------------------------------------------//
            /*Este bloco funciona de maneira similar ao anterior. Desta vez a lista com
             * valores ponderados retorna um tipo de voo.
             */
            ListaAleatoriaPonderada<TipoVoo> probabilidadeTipoVoo = new ListaAleatoriaPonderada<>();

            probabilidadeTipoVoo.adicionar(TipoVoo.ECONOMICO, TipoVoo.ECONOMICO.getProporcaoVoos());//[0.0, 7.5]
            probabilidadeTipoVoo.adicionar(TipoVoo.EXECUTIVO, TipoVoo.EXECUTIVO.getProporcaoVoos());//(7.5, 9.5]
            probabilidadeTipoVoo.adicionar(TipoVoo.PRIMEIRA_CLASSE, TipoVoo.PRIMEIRA_CLASSE.getProporcaoVoos()); //(9.5, 10.0]

            //----------------------------------------------------------------------------------------------------------//
            /* Aqui acontece as declarações e inicializações que serão utilizadas
             * durante o loop de criação de voos
             */
            gerarAeroportos("src/main/resources/templates/statics/aeroportos.json");
            List<Voo> voos = new ArrayList<>(); //Para fins de teste, uma lista com todos os voos criados.

            List<String> chaves = new ArrayList<>(AEROPORTOS.keySet());
            int quantidadeChaves = chaves.size();

            Aeroporto destino;
            boolean destinoValido = false;

            LocalDate now = LocalDate.now(); //Os voos serão criados a partir da data atual
            LocalDate dataVoo;
            LocalTime horaVoo;
            TipoVoo classeVoo;
            TipoVoo[] tiposVoo = TipoVoo.values();

            //-------------------------------------------------------------------------------------//
            for (Map.Entry<String, Aeroporto> entrada : AEROPORTOS.entrySet()) { //Cada elemento desse loop é um aeroporto
                Aeroporto origem = entrada.getValue();
                for (int i = 0; i < dias; i++) { //Este loop representa cada dia
                    for (int j = 0; j < origem.mediaVoosDiarios() / 2; j++) { //Este loop representa cada voo de ida do aeroporto

                        //--------------------------------------------------------------------------------------------------//
                        /* Este loop gera um destino válido. Um aeroporto que possui uma movimentação de +130000
                         * passageiros por ano pode gerar um voo para qualquer aeroporto que esteja a
                         * pelo menos 70km de distância.
                         * Caso o aeroporto possua uma movimentação de passageiros inferior a esta, a distância máxima
                         * que um voo poderá ser criado é de 900km.
                         */
                        do {
                            destino = AEROPORTOS.get(chaves.get((int) (Math.random() * quantidadeChaves)));

                            if (!destino.equals(origem) && origem.distancia(destino) >= 70)
                                if (origem.numeroPassageirosAnoOriginal >= 130000)
                                    destinoValido = true;
                                else if (origem.distancia(destino) < 900)
                                    destinoValido = true;

                        } while (!destinoValido);

                        //--------------------------------------------------------------------------------------------------//

                        dataVoo = now.plusDays(i);
                        LocalTime[] tempo = probabilidadeHorario.obter();
                        horaVoo = gerarHorarioValido(tempo);
                        LocalDateTime horarioVoo = LocalDateTime.of(dataVoo, horaVoo);
                        classeVoo = probabilidadeTipoVoo.obter();

                        try {
                            Voo voo = new Voo(classeVoo, origem, destino, horarioVoo);
                            voos.add(voo);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            return voos;
        }

        public static LocalTime gerarHorarioValido(LocalTime[] intervaloPossivel) {
            int horarioMaximo = (intervaloPossivel[1].getHour() * 60) + intervaloPossivel[1].getMinute();
            int horarioMinimo = (intervaloPossivel[0].getHour() * 60) + intervaloPossivel[0].getMinute();

            int intervaloPossivelMinutos = horarioMaximo - horarioMinimo;
            int minutos = (int) (Math.random() * intervaloPossivelMinutos);
            minutos = Uteis.arredondarParaProximo5(minutos);


            int horarioHora = (int) Math.floor((double) minutos / 60);
            int horarioMinutos = (int) ((((double) minutos / 60) - horarioHora) * 60);
            horarioMinutos = Uteis.arredondarParaProximo5(horarioMinutos);

            /*
             * Esta operação é necessária pois o valor de um parâmetro é uma referência ao objeto
             * e não o objeto em si. Ao solicitar uma nova instância idêntica, o objeto original continuará intacto,
             * mantendo seu status de constante.
             */
            LocalTime horarioValido = LocalTime.of(intervaloPossivel[0].getHour(), intervaloPossivel[0].getMinute());
            horarioValido = horarioValido.plusHours(horarioHora).plusMinutes(horarioMinutos);

            return horarioValido;
        }

        /**
         * A partir dessa classe é possível gerar vários novos objetos Aeroporto
         * usando um arquivo JSON
         */
        private static Aeroporto[] gerarAeroportos(String localArquivo) {
            StringBuilder aeroportoJson = new StringBuilder();
            File file = new File(localArquivo);

            try (Scanner scanner = new Scanner(file)) {
                while (scanner.hasNextLine()) {
                    aeroportoJson.append(scanner.nextLine());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            Aeroporto[] aeroportosArquivo = new Gson().fromJson(String.valueOf(aeroportoJson), Aeroporto[].class);

            List<Aeroporto> aeroportos = new ArrayList<>();
            Collections.addAll(aeroportos, aeroportosArquivo);
            aeroportos.sort(null);

            for (Aeroporto aeroporto : aeroportos) {
                AEROPORTOS.put(aeroporto.sigla, aeroporto);
                VOOS.adicionarVertice(aeroporto);
            }

            return aeroportosArquivo;
        }
    }
}
