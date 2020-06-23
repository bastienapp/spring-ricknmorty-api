package com.wildcodeschool.ricknmorty.controller;

import com.wildcodeschool.ricknmorty.entity.Character;
import com.wildcodeschool.ricknmorty.entity.Episode;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Random;

@Controller
public class MainController {

    @GetMapping("/")
    public String home(Model out) {

        String url = "https://rickandmortyapi.com";
        WebClient webClient = WebClient.create(url);

        final int[] ints = new Random().ints(1, 591).distinct().limit(6).toArray();

        Mono<List<Character>> call = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/character/{id1},{id2},{id3},{id4},{id5},{id6}")
                        .build(ints[0], ints[1], ints[2], ints[3], ints[4], ints[5]))
                .retrieve()
                .bodyToFlux(Character.class)
                .collectList();
        List<Character> characters = call.block();
        if (characters != null) {
            for (Character character : characters) {
                character.setFirstEpisode(getEpisodeFromUrl(character.getEpisode().get(0)));
            }
        }
        out.addAttribute("characters", characters);
        return "home";
    }

    private Episode getEpisodeFromUrl(String url) {
        WebClient webClient = WebClient.create(url);
        Mono<Episode> call = webClient.get()
                .retrieve()
                .bodyToMono(Episode.class);

        return call.block();
    }
}
