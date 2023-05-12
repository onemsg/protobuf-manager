package com.onemsg.protobuf.manager;

import org.springframework.boot.Banner.Mode;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Launcher {

	public static void main(String[] args) {
		var app = new SpringApplication(Launcher.class);
		app.setBannerMode(Mode.OFF);
		app.run(args);
	}

}
