package org.launchcode.controllers;

import org.launchcode.models.Cheese;
import org.launchcode.models.Menu;
import org.launchcode.models.data.CheeseDao;
import org.launchcode.models.data.MenuDao;
import org.launchcode.models.forms.AddMenuItemForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RequestMapping(value = "menu")
@Controller
public class MenuController {

    @Autowired
    private MenuDao menuDao;

    @Autowired
    private CheeseDao cheeseDao;

    @RequestMapping(value = "")
    public String index(Model model) {
        model.addAttribute("menus", menuDao.findAll());
        model.addAttribute("title", "Menus");

        return "menu/index";
    }

    @RequestMapping(value = "add", method = RequestMethod.GET)
    public String displayAddMenuForm(Model model) {
        model.addAttribute("title", "Add Menu");
        model.addAttribute(new Menu());
        model.addAttribute("menus", menuDao.findAll());
        return "menu/add";
    }

    @RequestMapping(value = "add", method = RequestMethod.POST)
    public String processAddMenuForm(@ModelAttribute @Valid Menu newMenu, Errors errors, Model model) {

        if (errors.hasErrors()) {
            model.addAttribute("title", "Add Menu");
            return "menu/add";
        }

        menuDao.save(newMenu);
        return "redirect:view/" + newMenu.getId();
    }

    @RequestMapping(value = "/view/{menuId}", method = RequestMethod.GET)
    public String viewMenu(@PathVariable("menuId") int menuId, Model model) {
        Menu menu = menuDao.findOne(menuId);
        model.addAttribute("menu", menu);
        model.addAttribute("cheeses", menu.getCheeses());
        model.addAttribute("title", menu.getName());

        return "menu/view";
    }

    @RequestMapping(value = "add-item", method = RequestMethod.POST)
    public String processAddItem(Model model, @ModelAttribute @Valid AddMenuItemForm theMenu, Errors errors){

        if (errors.hasErrors()) {
            return "menu/add-item";
        }


        Cheese cheese = cheeseDao.findOne(theMenu.getCheeseId());
        Menu menu = menuDao.findOne(theMenu.getMenuId());

        menu.addItem(cheese);
        menuDao.save(menu);

        return "redirect:view/" + theMenu.getMenuId();
    }

    @RequestMapping(value = "/add-item/{menuId}", method = RequestMethod.GET)
    public String displayAddItem(@PathVariable("menuId") int menuId, Model model, Menu newMenu){
        Menu menu = menuDao.findOne(menuId);
        AddMenuItemForm menuItemForm = new AddMenuItemForm(menu, cheeseDao.findAll());
        model.addAttribute("title", menu.getName());
        model.addAttribute("menu", menu);
        model.addAttribute("form", menuItemForm);

        return "menu/add-item";
    }
}
