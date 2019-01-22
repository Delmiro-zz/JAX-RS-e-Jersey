package br.com.alura.loja;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import org.glassfish.grizzly.http.server.HttpServer;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import br.com.alura.loja.modelo.Carrinho;
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
	public void testaBuscaDeCarrinhoComRetornoEsperado(){
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target("http://localhost:8081/");
		
		String conteudo = target.path("/carrinhos").request().get(String.class);
		Carrinho carrinho = (Carrinho) new XStream().fromXML(conteudo);
		
		Assert.assertEquals("Rua Vergueiro 3185, 8 andar", carrinho.getRua());
	}
	
}
