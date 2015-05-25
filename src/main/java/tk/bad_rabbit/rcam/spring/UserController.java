package tk.bad_rabbit.rcam.spring;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
 
@Controller
public class UserController {
 
    //@Autowired
    //private UserService userService;
  
    /**
    * Request mapping for user
    */
    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public ModelAndView getUsersView() {
        ModelAndView mv = new ModelAndView("usersView");
        //mv.addObject("usersModel", userService.findAll());
        return mv;
    }
     
    /**
    * Rest web service
    */
    @RequestMapping(value = "/usersList", method = RequestMethod.GET)
    public @ResponseBody String getUsersRest() {
        return "hey hey its ok";
    }
}