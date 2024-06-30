package com.uce.edu.ec.ApiNasa;

import com.uce.edu.ec.ApiNasa.Consumer.ConsumirAPI;
import com.uce.edu.ec.ApiNasa.Model.Photo;
import com.uce.edu.ec.ApiNasa.View.MainFrameApp;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import javax.swing.*;
import java.util.List;

@SpringBootApplication
public class ApiNasaApplication {

	public static void main(String[] args) {

			SwingUtilities.invokeLater(() -> {

				ConsumirAPI apiConsum = new ConsumirAPI();


				List<Photo> photos = apiConsum.getPhotos();

				MainFrameApp mainFrame = new MainFrameApp(apiConsum);
				mainFrame.showMainFrame();
			});

	}
}
