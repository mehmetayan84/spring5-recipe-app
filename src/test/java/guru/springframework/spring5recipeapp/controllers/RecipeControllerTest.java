package guru.springframework.spring5recipeapp.controllers;

import guru.springframework.spring5recipeapp.commands.RecipeCommand;
import guru.springframework.spring5recipeapp.exceptions.NotFoundException;
import guru.springframework.spring5recipeapp.services.RecipeService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class RecipeControllerTest {

    @Mock
    RecipeService recipeService;

    RecipeController recipeController;

    MockMvc mockMVC;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        recipeController = new RecipeController(recipeService);
        mockMVC = MockMvcBuilders.standaloneSetup(recipeController)
                                .setControllerAdvice(new ControllerExceptionHandler())
                                .build();
    }

    @Test
    public void showById() throws Exception {

        RecipeCommand recipe = new RecipeCommand();

        recipe.setId(1L);

        when(recipeService.findCommandById(anyLong())).thenReturn(recipe);

        mockMVC.perform(get("/recipe/1/show"))
                .andExpect(status().isOk())
                .andExpect(view().name("recipe/show"))
                .andExpect(model().attributeExists("recipe"));
    }

    @Test
    public void testGetRecipeNotFound() throws Exception {

        RecipeCommand recipeCommand = new RecipeCommand();

        recipeCommand.setId(1L);

        when(recipeService.findCommandById(anyLong())).thenThrow(new NotFoundException());

        mockMVC.perform(get("/recipe/3/show"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetNewRecipe() throws Exception{

        RecipeCommand command = new RecipeCommand();

        mockMVC.perform(get("/recipe/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("recipe/recipeform"))
                .andExpect(model().attributeExists("recipe"));


    }

    @Test
    public void saveOrUpdate() throws Exception{

        RecipeCommand command = new RecipeCommand();

        command.setId(2L);

        when(recipeService.saveRecipeCommand(any())).thenReturn(command);

        mockMVC.perform(post("/recipe/")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("id", "")
                .param("description", "some string")
                .param("directions", "some direction"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/recipe/2/show"));
    }

    @Test
    public void getUpdateView() throws Exception {

        RecipeCommand command = new RecipeCommand();

        command.setId(2L);

        when(recipeService.findCommandById(anyLong())).thenReturn(command);

        mockMVC.perform(get("/recipe/2/update"))
                .andExpect(status().isOk())
                .andExpect(view().name("recipe/recipeform"))
                .andExpect(model().attributeExists("recipe"));
    }

    @Test
    public void testDelete() throws Exception {

        mockMVC.perform(get("/recipe/2/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/"));

        verify(recipeService, times(1)).deleteById(anyLong());

    }

    @Test
    public void handleNotFound() throws Exception {

        when(recipeService.findCommandById(anyLong())).thenThrow(NotFoundException.class);

        mockMVC.perform(get("/recipe/1/show"))
                .andExpect(status().isNotFound())
                .andExpect(view().name("404error"));

    }

    @Test
    public void handleNumberFormat() throws Exception {

        mockMVC.perform(get("/recipe/asd/show"))
                .andExpect(status().isBadRequest())
                .andExpect(view().name("400error"));

    }
}