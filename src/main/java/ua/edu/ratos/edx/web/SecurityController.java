package ua.edu.ratos.edx.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import ua.edu.ratos.edx.domain.*;
import ua.edu.ratos.edx.domain.Class;
import ua.edu.ratos.edx.repository.LocalClassesHolder;
import ua.edu.ratos.edx.repository.LocalFacultiesHolder;
import ua.edu.ratos.edx.repository.LocalOrganisationsHolder;
import ua.edu.ratos.edx.repository.LocalStudentHolder;
import javax.servlet.http.HttpServletRequest;
import javax.validation.*;
import java.security.Principal;
import java.util.Enumeration;
import java.util.Set;

@Controller
public class SecurityController {

    private static final Log LOG = LogFactory.getLog(SecurityController.class);

    @GetMapping("/login-custom")
    public ModelAndView login(@RequestParam(value = "error", required = false, defaultValue = "false") boolean error, Principal principal) {
        LOG.debug("Custom log-in, Principal :: "+principal);
        ModelAndView model = new ModelAndView("my-sign-in");
        if (error) {
            model.addObject("error", true);
            return model;
        }
        return model;
    }

    @GetMapping("/access-denied")
    @ResponseBody
    public String denied(Principal principal) {
        LOG.warn("Principal denied:: "+principal);
        return "Access is denied, not enough authority";
    }

    @Autowired
    private LocalOrganisationsHolder localOrganisationsHolder;

    @ModelAttribute("organisations")
    public Set<Organisation> populateOrg() {
        return localOrganisationsHolder.getAll();
    }

    @Autowired
    private LocalFacultiesHolder localFacultiesHolder;

    @ModelAttribute("faculties")
    public Set<Faculty> populateFac() {
        return localFacultiesHolder.getAll();
    }

    @Autowired
    private LocalClassesHolder localClassesHolder;

    @ModelAttribute("classes")
    public Set<Class> populateClass() {
        return localClassesHolder.getAll();
    }

    @GetMapping("/sign-up/student")
    public ModelAndView signUp( Principal principal) {
        LOG.debug("Custom sign-up, Principal :: "+principal);
        User user = new User();
        ModelAndView model = new ModelAndView("my-sign-up", "student", new Student(user));
        model.addObject("unexpectedError", "no errors");
        return model;
    }


    @Autowired
    private LocalStudentHolder localStudentHolder;

    @PostMapping("/sign-up/accept")
    public String signUpAccept(@Valid Student student, BindingResult result, Model model) {

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<User>> constraintViolations = validator.validate(student.getUser());
        LOG.debug("Violations number :: "+constraintViolations.size());

        LOG.debug("Sign up received :: "+student);
        if (result.hasErrors()) {
            LOG.error("# of errors is: "+result.getFieldErrorCount());
            result.getAllErrors().forEach(err->LOG.error("errors is :: "+err.toString()));
            return "my-sign-up";
        }
        localStudentHolder.addStudent(student.getUser().getEmail(), student);
        LOG.debug("Saved student :: "+student);
        return "redirect:/login-custom";
    }



    //---------- TEST-----------

    @GetMapping("/student/start")
    public String start(Principal principal, Model model) {
        LOG.debug("Principal :: "+principal);
        model.addAttribute("username", principal.getName());
        return "start";
    }

    @GetMapping("/student/start/angular")
    public String startAngJS(Principal principal, Model model) {
        LOG.debug("Principal :: "+principal);
        model.addAttribute("username", principal.getName());
        return "start-ang-js";
    }

    @GetMapping("/student/start/react")
    public String startReactJS(Principal principal, Model model) {
        LOG.debug("Principal :: "+principal);
        model.addAttribute("username", principal.getName());
        return "start-react-js";
    }



    @GetMapping("/student/test")
    @ResponseBody
    public String test(Principal principal) {
        LOG.debug("Principal :: "+principal);
        return "Here comes the first question...";
    }

    @GetMapping("/student/data")
    @ResponseBody
    public String data(Principal principal) {
        LOG.debug("Principal :: "+principal);
        return "OK data returned";
    }

    @GetMapping("/admin/start")
    @ResponseBody
    public String admin(Principal principal) {
        LOG.debug("Principal :: "+principal);
        return "admin";
    }
    
    //-------------------- DEBUG-----------------
    
    @PostMapping("/ratos/receive")
    @ResponseStatus(code = HttpStatus.OK)
    @ResponseBody
    public void receiveScore(@RequestBody String body, HttpServletRequest request) throws Exception {
    	LOG.debug("headers ::");
        Enumeration<String> headerNames = request.getHeaderNames();
        if (headerNames != null) {
            while (headerNames.hasMoreElements()) {
                String nextElement = headerNames.nextElement();
                LOG.debug("Header :: " + nextElement + " ::"
                        + request.getHeader(nextElement));
            }
        }
        LOG.debug("body :: " + body);
       
    }

}
