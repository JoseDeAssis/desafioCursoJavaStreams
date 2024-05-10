package br.com.jose.fipeProject.principal;

import br.com.jose.fipeProject.model.Dados;
import br.com.jose.fipeProject.model.DadosVeiculo;
import br.com.jose.fipeProject.model.Modelos;
import br.com.jose.fipeProject.service.ConsumoApi;
import br.com.jose.fipeProject.service.ConverteDados;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Principal {

    private Scanner scanner = new Scanner(System.in);
    private ConsumoApi consumoApi = new ConsumoApi();
    private ConverteDados converteDados = new ConverteDados();
    private final String ADRESS = "https://parallelum.com.br/fipe/api/v1";

    public void showMenu() {
        System.out.println("**** OPÇÕES ****\n" +
                "Carro\n" +
                "Moto\n" +
                "Caminhão\n" +
                "\n" +
                "Digite uma das opções para consultar valores:");
        var opcao = scanner.nextLine();
        var endereco = ADRESS;

        switch (opcao.toLowerCase()) {
            case "carro":
                endereco = endereco + "/carros/marcas";
                break;

            case "moto":
                endereco = endereco + "/motos/marcas";
                break;

            case "caminhão":
                endereco = endereco + "/caminhoes/marcas";
                break;

            default:
                System.out.println("Valor passado inválido!");
                return;
        }

        var json = consumoApi.obterDados(endereco);
        var dadosMarcas = converteDados.obterLista(json, Dados.class);
        dadosMarcas.stream()
                .sorted(Comparator.comparing(Dados::codigo))
                .forEach(System.out::println);

        System.out.println("\nInforme o código da marca para a consulta:");
        var codigoMarca = scanner.nextLine();
        endereco = endereco + "/" + codigoMarca + "/modelos";
        json = consumoApi.obterDados(endereco);

        System.out.println(json);
        var dadosModelos = converteDados.obterDados(json, Modelos.class);
        dadosModelos.modelos().stream()
                .sorted(Comparator.comparing(Dados::codigo))
                .forEach(System.out::println);

        System.out.println("\nDigite um trecho do nome do carro a ser buscado:");
        var nomeModelo = scanner.nextLine();
        System.out.println("\nModelos filtrados:");
        List <Dados> modelosFiltrados = dadosModelos.modelos().stream()
                        .filter(m -> m.nome().toLowerCase().contains(nomeModelo.toLowerCase()))
                        .toList();
        modelosFiltrados.forEach(System.out::println);

        System.out.println("\nInforme o código do modelo para a consulta:");
        var codigoModelo = scanner.nextLine();
        endereco = endereco + "/" + codigoModelo + "/anos";
        json = consumoApi.obterDados(endereco);
        var dadosAnos = converteDados.obterLista(json, Dados.class);

        List<DadosVeiculo> veiculos = new ArrayList<>();
        for (Dados dadosAno : dadosAnos) {
            json = consumoApi.obterDados(endereco + "/" + dadosAno.codigo());
            var dadosVeiculos = converteDados.obterDados(json, DadosVeiculo.class);
            veiculos.add(dadosVeiculos);
        }

        veiculos.forEach(System.out::println);

    }
}
