package br.com.alura.loja;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.glassfish.grizzly.http.server.HttpServer;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import br.com.alura.loja.modelo.Carrinho;
import br.com.alura.loja.modelo.Produto;
import com.thoughtworks.xstream.XStream;


public class ClienteTeste {

	private HttpServer server;
	
	@Before
	public void startServidor(){
		this.server = Servidor.inicializaServidor();
	}
	
	@After
	public void stopServidor(){
		this.server.stop();
	}
	
	
	@Test
	public void testaBuscaDeCarrinhoComRetornoEsperadoXML(){
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target("http://localhost:8084");
		
		String conteudo = target.path("/carrinhos/1").request().get(String.class);
		Carrinho carrinho = (Carrinho) new XStream().fromXML(conteudo);
		Assert.assertEquals("Rua Vergueiro 3185, 8 andar", carrinho.getRua());
		
	}
	
	@Test
	public void testaEntradaDeNovosCarrinhos(){
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target("http://localhost:8084");
		
		Carrinho carrinho = new Carrinho();
		carrinho.adiciona(new Produto(314L, "Microfone", 999, 1));
		carrinho.setRua("Rua Vergueiro");
		carrinho.setCidade("Sao Paulo");
		carrinho.setId(1);
		
		//Transforma o carrinho em xml
		String xml = carrinho.toXML();
		//Simula a representação da URI
		Entity<String> entity = Entity.entity(xml, MediaType.APPLICATION_XML);
		//Realiza uma requisição do tipo POST
		Response response = target.path("/carrinhos").request().post(entity);
		//Verifica se requisição foi com sucesso
		Assert.assertEquals(201, response.getStatus());
		
		//Recupera a URI da requisição
		String location = response.getHeaderString("Location");
		//Recupera o objeto que tá na URI
		String conteudo = client.target(location).request().get(String.class);
		//Verifica se o retorno tem o objeto passado
		Assert.assertTrue(conteudo.contains("Microfone"));
	}
}
