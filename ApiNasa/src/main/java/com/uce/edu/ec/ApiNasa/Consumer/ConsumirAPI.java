package com.uce.edu.ec.ApiNasa.Consumer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.uce.edu.ec.ApiNasa.Model.Photo;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

@Service
public class ConsumirAPI {

    private static final String API_URL = "https://api.nasa.gov/mars-photos/api/v1/rovers/curiosity/photos?sol=1000&api_key=DEMO_KEY";

    public List<Photo> getPhotos() {
        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(API_URL, String.class);

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response);
            JsonNode photos = root.path("photos");
            List<Photo> photoList = mapper.convertValue(photos, mapper.getTypeFactory().constructCollectionType(List.class, Photo.class));


            photoList.forEach(photo -> {
                String imageUrl = photo.getImg_src();
                if (imageUrl != null && imageUrl.startsWith("http://")) {
                    photo.setImg_src(imageUrl.replaceFirst("http://", "https://"));
                }
            });

            return Collections.unmodifiableList(photoList);
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}

