package ua.edu.ratos.edx.web;


import java.security.Principal;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class LTILaunchController {

	@GetMapping("/student/start")
	public String start(Principal principal) {
		System.out.println("Principal :: "+principal);
		return "start";
	}

	@GetMapping("/student/test")
	@ResponseBody
	public String test(Principal principal) {
		System.out.println("Principal :: "+principal);
		return "Here comes the first question...";
	}


	@CrossOrigin({"http://localhost:18010", "http://localhost"})
	@PostMapping("/lti/1p0/launch")
	public String startPost(HttpServletRequest request, Principal principal) throws Exception {
		System.out.println("Principal :: "+principal);
		printParameters(request);
		return "redirect:/student/start";
	}

	private void printParameters(HttpServletRequest request) {
		Map params = request.getParameterMap();
		Iterator i = params.keySet().iterator();
		while ( i.hasNext() ) {
			String key = (String) i.next();
			String value = ((String[]) params.get( key ))[ 0 ];
			System.out.println("key :: "+key);
			System.out.println("value :: "+value);
		}
	}

}
