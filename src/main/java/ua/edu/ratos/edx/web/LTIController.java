package ua.edu.ratos.edx.web;


import java.security.Principal;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class LTIController {

	@GetMapping("/student/start")
	public String start() {
		return "start";
	}

	@GetMapping("/student/test")
	@ResponseBody
	public String test() {
		return "OK";
	}


	@CrossOrigin({"http://localhost:18010", "http://localhost"})
	@PostMapping("/lti1p0/launch")
	public String startPost(HttpServletRequest request, Principal principal) throws Exception {
		printParameters(request);
		System.out.println("Principal.class :: "+principal.getClass());
		System.out.println("Principal :: "+principal);
		// here should work
		return "redirect:/student/start";
	}

	@PostMapping("/ratos/receive")
	@ResponseBody
	public String receiveScore(@RequestBody String body, HttpServletRequest request) throws Exception {
		System.out.println("body :: " + body);
		System.out.println("headers ::");
		Enumeration<String> headerNames = request.getHeaderNames();
		if (headerNames != null) {
			while (headerNames.hasMoreElements()) {
				String nextElement = headerNames.nextElement();
				System.out.println("Header :: " + nextElement + " ::"
						+ request.getHeader(nextElement));
			}
		}
		return "OK";
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
