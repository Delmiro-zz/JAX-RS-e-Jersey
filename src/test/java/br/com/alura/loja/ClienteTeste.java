package br.com.alura.loja;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.filter.LoggingFilter;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import br.com.alura.loja.modelo.Carrinho;
import br.com.alura.loja.modelo.Produto;


public class ClienteTeste {

	private HttpServer server;
	private WebTarget target;
	private Client client;
	
	@Before
	public void startServidor(){
		this.server = Servidor.inicializaServidor();
		ClientConfig config = new ClientConfig();
		config.register(new LoggingFilter());
		this.client = ClientBuilder.newClient(config);
		this.target = client.target("http://localhost:8084");
	}
	
	@After
	public void stopServidor(){
		this.server.stop();
	}
	
	
	@Test
	public void testaBuscaDeCarrinhoComRetornoEsperadoXML(){
		Carrinho carrinho = target.path("/carrinhos/1").request().get(Carrinho.class);
		Assert.assertEquals("Rua Vergueiro 3185, 8 andar", carrinho.getRua());
	}
	
	@Test
	public void testaEntradaDeNovosCarrinhos(){
		Carrinho carrinho = new Carrinho();
		carrinho.adiciona(new Produto(314L, "Microfone", 999, 1));
		carrinho.setRua("Rua Vergueiro");
		carrinho.setCidade("Sao Paulo");
		carrinho.setId(1);
		
		//Simula a representação da URI
		Entity<Carrinho> entity = Entity.entity(carrinho, MediaType.APPLICATION_XML);
		//Realiza uma requisição do tipo POST
		Response response = target.path("/carrinhos").request().post(entity);
		//Verifica se requisição foi com sucesso
		Assert.assertEquals(201, response.getStatus());
		
		//Recupera a URI da requisição
		String location = response.getHeaderString("Location");
		//Recupera o objeto que tá na URI
		Carrinho carrinhoCarregado = client.target(location).request().get(Carrinho.class);
		//Verifica se o retorno tem o objeto passado
		Assert.assertEquals("Microfone", carrinhoCarregado.getProdutos().get(0).getNome());
	}
}
