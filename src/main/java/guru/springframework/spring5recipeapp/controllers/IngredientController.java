package guru.springframework.spring5recipeapp.controllers;

import guru.springframework.spring5recipeapp.commands.IngredientCommand;
import guru.springframework.spring5recipeapp.commands.RecipeCommand;
import guru.springframework.spring5recipeapp.commands.UnitOfMeasureCommand;
import guru.springframework.spring5recipeapp.services.IngredientService;
import guru.springframework.spring5recipeapp.services.RecipeService;
import guru.springframework.spring5recipeapp.services.UnitOfMeasureService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@Slf4j
@RequestMapping("/recipe")
public class IngredientController {

    RecipeService recipeService;
    IngredientService ingredientService;
    UnitOfMeasureService unitOfMeasureService;

    public IngredientController(RecipeService recipeService, IngredientService ingredientService,
                                UnitOfMeasureService unitOfMeasureService) {
        this.recipeService = recipeService;
        this.ingredientService = ingredientService;
        this.unitOfMeasureService = unitOfMeasureService;
    }

    @GetMapping
    @RequestMapping("/{recipeId}/ingredients")
    public String listIngredients(@PathVariable String recipeId, Model model) {

        log.debug("Getting ingredient list of the recipe: " + recipeId);

        model.addAttribute("recipe", recipeService.findCommandById(Long.valueOf(recipeId)));

        return "/recipe/ingredient/list";

    }

    @GetMapping
    @RequestMapping("/{recipeId}/ingredients/{id}/show")
    public String showIngredient(@PathVariable String recipeId, @PathVariable String id, Model model) {

        log.debug("Getting the ingredient with the id: " + id);

        model.addAttribute("ingredient", ingredientService.findByRecipeIdAndIngredientId(Long.valueOf(recipeId),
                            Long.valueOf(id)));

        return "/recipe/ingredient/show";

    }

    @GetMapping
    @RequestMapping("/{recipeId}/ingredients/{id}/update")
    public String showIngredientUpdateForm(@PathVariable String recipeId, @PathVariable String id, Model model) {

        log.debug("Preparing ingredient form for ingredient with id: " + id);

        model.addAttribute("ingredient", ingredientService.findByRecipeIdAndIngredientId(Long.valueOf(recipeId),
                Long.valueOf(id)));

        model.addAttribute("uomList", unitOfMeasureService.findAllCommand());

        return "/recipe/ingredient/ingredientform";
    }

    @PostMapping
    @RequestMapping("/{recipeId}/ingredient")
    public String saveOrUpdate(@ModelAttribute IngredientCommand ingredientCommand) {

        IngredientCommand savedCommand = ingredientService.saveIngredientCommand(ingredientCommand);

        return "redirect:/recipe/" + ingredientCommand.getRecipeId() + "/ingredients/"+ savedCommand.getId() + "/show";
    }

    @GetMapping
    @RequestMapping("/{recipeId}/ingredient/new")
    public String newIngredient(@PathVariable String recipeId, Model model) {

        RecipeCommand recipeCommand = recipeService.findCommandById(Long.valueOf(recipeId));

        IngredientCommand ingredientCommand = new IngredientCommand();

        ingredientCommand.setRecipeId(recipeCommand.getId());

        ingredientCommand.setUnitOfMeasure(new UnitOfMeasureCommand());

        model.addAttribute("recipe", recipeCommand);

        model.addAttribute("ingredient", ingredientCommand);

        model.addAttribute("uomList", unitOfMeasureService.findAllCommand());

        return "/recipe/ingredient/ingredientform";

    }

    @GetMapping
    @RequestMapping("/{recipeId}/ingredients/{id}/delete")
    public String deleteIngredient(@PathVariable String recipeId, @PathVariable String id) {

        log.debug("Deleting ingredient with the id: "+ id + " of recipe with the id of: " + recipeId);

        ingredientService.deleteIngredient(Long.valueOf(recipeId), Long.valueOf(id));

        return "redirect:/recipe/" + recipeId + "/ingredients";
    }
}
