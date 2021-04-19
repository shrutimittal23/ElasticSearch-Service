package com.javainuse.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.javainuse.model.User;

import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.JestResult;
import io.searchbox.client.config.HttpClientConfig;
import io.searchbox.core.Delete;
import io.searchbox.core.Get;
import io.searchbox.core.Index;
import io.searchbox.core.Update;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
public class UserController {

	private static final Logger log = LoggerFactory.getLogger(UserController.class);

	JestClient client = null;

	public JestClient getClient() {
		if (this.client == null) {
			log.info("--- setting up user ---");
			JestClientFactory factory = new JestClientFactory();
			factory.setHttpClientConfig(new HttpClientConfig.Builder(
					"https://search-ytsearch-staging-vflomzxcm3c4pklej6nwyomxfm.us-east-1.es.amazonaws.com")
							.multiThreaded(true).defaultMaxTotalConnectionPerRoute(2).maxTotalConnection(10).build());
			this.client = factory.getObject();
			return factory.getObject();
		}
		return this.client;

	}

	@PostMapping("/save")
	public String saveUser(@RequestBody User user) throws IOException {
		log.info("--- saving user ---");
		JestClient client = this.getClient();
		ObjectMapper mapper = new ObjectMapper();
		JsonNode userNode = mapper.createObjectNode().put("name", user.getName()).put("email", user.getEmail());
		JestResult postResult = client
				.execute(new Index.Builder(userNode.toString()).index("data").type("employee").build());

		return postResult.toString();
	}

	@GetMapping("/find/{id}")
	public String findUser(@PathVariable final String id) throws IOException {
		log.info("--- finding user ---");
		JestClient client = this.getClient();
		JestResult getResult = client.execute(new Get.Builder("data", id).type("employee").build());
		return getResult.toString();
	}

	@PutMapping("/update/{id}")
	public String updateUser(@PathVariable final String id, @RequestBody User user) throws IOException {
		log.info("--- updating user ---");
		JestClient client = this.getClient();
		ObjectMapper mapper = new ObjectMapper();
		JsonNode userNode = mapper.createObjectNode().put("name", user.getName()).put("email", user.getEmail());
		JestResult putResult = client.execute(new Update.Builder(userNode.toString()).index("data").id(id).build());

		return putResult.toString();
	}

	@DeleteMapping("/delete/{id}")
	public String deleteUser(@PathVariable final String id, @RequestBody User user) throws IOException {
		log.info("--- deleting user ---");
		JestClient client = this.getClient();

		JestResult deleteResult = client.execute(new Delete.Builder(id).index("data").type("employee").build());
		return deleteResult.toString();
	}
}
