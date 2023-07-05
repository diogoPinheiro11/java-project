package pt.ipvc.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import pt.ipvc.bll.UsersBLL;
import pt.ipvc.dal.Users;
import pt.ipvc.models.UserProfileModel;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Controller
public class UserProfileController {

    @GetMapping("/userProfile")
    public String userProfile(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession();
        String userName = (String) session.getAttribute("userName");

        Users currentUser = UsersBLL.getByName(userName);

        UserProfileModel user = new UserProfileModel();
        user.setName(currentUser.getName());
        user.setEmail(currentUser.getEmail());
        user.setPhone(String.valueOf(currentUser.getPhone()));
        user.setDoor(Optional.ofNullable(currentUser.getDoor()).orElse(""));
        user.setStreet(currentUser.getStreet());
        user.setLocation(Optional.ofNullable(currentUser.getLocation()).orElse(""));
        user.setPassword(currentUser.getPassword());
        model.addAttribute("user", user);

        return "userProfile";
    }

    @PostMapping(value = "/editProfile")
    public String updateUser(@Valid @ModelAttribute("user") UserProfileModel user, BindingResult result, Model model, HttpSession session) {

        if (result.hasErrors()) {
            result.getAllErrors().forEach(System.out::println);
            return "userProfile";
        }

        String userName = (String) session.getAttribute("userName");
        Users users = UsersBLL.getByName(userName);

        if(users == null) {
            return "redirect:/login";
        }

        boolean emailExists = !users.getEmail().equals(user.getEmail()) && UsersBLL.checkEmail(user.getEmail());
        boolean phoneExists = !String.valueOf(users.getPhone()).equals(user.getPhone()) && UsersBLL.checkPhone(String.valueOf(user.getPhone()));

        if (emailExists) {
            result.rejectValue("email", "error.user", "Email already in use!");

        } else if (phoneExists) {
            result.rejectValue("telemovel", "error.user", "Phone already in use!");
        } else {
            Users currentUser = UsersBLL.getByName(userName);
            currentUser.setName(user.getName());
            currentUser.setEmail(user.getEmail());
            currentUser.setPhone(user.getPhone());
            currentUser.setPassword(user.getPassword());
            currentUser.setDoor(user.getDoor());
            currentUser.setStreet(user.getStreet());
            currentUser.setLocation(user.getLocation());

            if(!user.getPassword().isBlank())
                currentUser.setPassword(user.getPassword());


            UsersBLL.update(currentUser);

            return "login";

        }

        return "userProfile";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession();
        session.removeAttribute("userName");

        return "redirect:/login";
    }
}
