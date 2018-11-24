package guru.springframework.spring5recipeapp.controllers;

import guru.springframework.spring5recipeapp.commands.RecipeCommand;
import guru.springframework.spring5recipeapp.exceptions.NotFoundException;
import guru.springframework.spring5recipeapp.services.RecipeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;

@Slf4j
@Controller
@RequestMapping("/recipe/")
public class RecipeController {

    private final RecipeService recipeService;

    private final static String RECIPE_FORM_URL = "/recipe/recipeform";

    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    @RequestMapping("{id}/show")
    public String showById(@PathVariable String id, Model model) throws NumberFormatException{
        Long.parseLong(id);
        model.addAttribute("recipe", recipeService.findCommandById(new Long(id)));
        return "recipe/show";
    }

    @RequestMapping("new")
    public String newRecipe(Model model) {
        model.addAttribute("recipe", new RecipeCommand());
        return RECIPE_FORM_URL;
    }

    @RequestMapping(name = "", method = RequestMethod.POST)
    public String saveOrUpdate(@Valid @ModelAttribute("recipe") RecipeCommand command, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(error ->
                    log.error("One of the error is : " + error));
            return RECIPE_FORM_URL;
        }

        RecipeCommand savedCommand = recipeService.saveRecipeCommand(command);

        return "redirect:/recipe/" + savedCommand.getId() + "/show";
    }

    @RequestMapping("{id}/update")
    public String getUpdateView(@PathVariable String id, Model model) {
       model.addAttribute("recipe", recipeService.findCommandById(new Long(id)));
       return RECIPE_FORM_URL;
    }


    @RequestMapping("/{id}/delete")
    public String delete(@PathVariable String id) {

        log.debug("Deleting recipe with the id: "+ id);

        recipeService.deleteById(Long.valueOf(id));

        return "redirect:/";
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    public ModelAndView handleNotFound(Exception exception) {

        log.error("Handling not found exception");
        log.error(exception.getMessage());

        ModelAndView modelAndView = new ModelAndView();

        modelAndView.setViewName("404error");

        modelAndView.addObject("exception", exception);

        return modelAndView;

    }

}
