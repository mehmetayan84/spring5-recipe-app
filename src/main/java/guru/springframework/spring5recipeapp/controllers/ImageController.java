package guru.springframework.spring5recipeapp.controllers;

import guru.springframework.spring5recipeapp.commands.RecipeCommand;
import guru.springframework.spring5recipeapp.services.ImageService;
import guru.springframework.spring5recipeapp.services.RecipeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Controller
public class ImageController {

    private final RecipeService recipeService;

    private final ImageService imageService;

    public ImageController(RecipeService recipeService, ImageService imageService) {
        this.imageService = imageService;
        this.recipeService = recipeService;
    }

    @GetMapping("/recipe/{id}/image")
    public String getImageForm(@PathVariable String id, Model model) {

        model.addAttribute("recipe", recipeService.findCommandById(Long.valueOf(id)));

        return "/recipe/imageuploadform";
    }

    @PostMapping("/recipe/{id}/image")
    public String postImageForm(@PathVariable String id, @RequestParam("imagefile") MultipartFile imageFile) {

        imageService.saveImageFile(Long.valueOf(id), imageFile);

        return "redirect:/recipe/" + id + "/show";
    }

    @GetMapping("/recipe/{id}/recipeimage")
    public void renderImage(@PathVariable String id, HttpServletResponse response) throws IOException {

        RecipeCommand recipeCommand = recipeService.findCommandById(Long.valueOf(id));

        byte[] byteArray = new byte[recipeCommand.getImage().length];

        int i = 0;

        for(byte b : recipeCommand.getImage()) {
            byteArray[i++] = b;
        }

        response.setContentType("image/jpeg");

        InputStream stream = new ByteArrayInputStream(byteArray);

        org.apache.tomcat.util.http.fileupload.IOUtils.copy(stream, response.getOutputStream());

    }

}
