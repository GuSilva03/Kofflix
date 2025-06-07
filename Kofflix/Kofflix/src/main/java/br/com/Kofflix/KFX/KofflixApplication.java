package br.com.Kofflix.KFX;


import br.com.Kofflix.KFX.principal.Principal;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class KofflixApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(KofflixApplication.class, args);
	}
	@Override
	public void run(String... args) throws Exception {
		Principal principal = new Principal();
		principal.showMenu();
	}
}
