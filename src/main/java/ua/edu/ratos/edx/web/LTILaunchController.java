package ua.edu.ratos.edx.web;

import java.security.Principal;
import java.util.Iterator;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;


@Controller
public class LTILaunchController {
	
	private static final Log LOG = LogFactory.getLog(LTILaunchController.class);
	
	
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
	
	@GetMapping("/sign-up")
	public ModelAndView signUp( Principal principal) {
		LOG.debug("Custom sign-up, Principal :: "+principal);
		ModelAndView model = new ModelAndView("my-sign-up");
		return model;
	}
	
	@PostMapping("/sign-up-accept")
	@ResponseBody
	public void signUpAccept(@RequestBody Object body) {
		LOG.debug(body);
	}
	
	@GetMapping("/admin/start")
	@ResponseBody
	public String admin(Principal principal) {
		LOG.debug("Principal :: "+principal);
		return "admin";
	}

	@GetMapping("/student/start")
	public String start(Principal principal) {
		LOG.debug("Principal :: "+principal);
		return "start";
	}

	@GetMapping("/student/test")
	@ResponseBody
	public String test(Principal principal) {
		LOG.debug("Principal :: "+principal);
		return "Here comes the first question...";
	}
	
	@GetMapping("/accessDenied")
	@ResponseBody
	public String denied(Principal principal) {
		LOG.debug("Principal :: "+principal);
		return "Denied, not enough authority";
	}


	@CrossOrigin({"http://localhost:18010", "http://localhost"})
	@PostMapping("/lti/1p0/launch")
	public String startPost(HttpServletRequest request, Authentication principal) throws Exception {
		LOG.debug("Authentication :: "+principal);
		printParameters(request);
		return "redirect:/student/start";
	}

	private void printParameters(HttpServletRequest request) {
		Map<String, String[]> params = request.getParameterMap();
		Iterator<String> i = params.keySet().iterator();
		while (i.hasNext()) {
			String key = (String) i.next();
			String value = ((String[]) params.get( key ))[ 0 ];
			LOG.debug("key :: "+key);
			LOG.debug("value :: "+value);
		}
	}
	

}
