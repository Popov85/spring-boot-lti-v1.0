package ua.edu.ratos.edx.web;

import java.util.Iterator;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import ua.edu.ratos.edx.security.lti.LTIToolConsumerCredentials;


@Controller
public class LTILaunchController {
	
	private static final Log LOG = LogFactory.getLog(LTILaunchController.class);

	@CrossOrigin({"http://localhost:18010", "http://localhost"})
	@PostMapping("/lti/1p0/launch")
	public String startPost(HttpServletRequest request, Authentication authentication, HttpSession session) throws Exception {
		
		LTIToolConsumerCredentials principal = (LTIToolConsumerCredentials) authentication.getPrincipal();
		
		LOG.debug("Authentication HashCode :: "+authentication.hashCode());
		LOG.debug("Session ID :: "+session.getId());
		LOG.debug("SourcedId :: "+principal.getOutcome().get().getSourcedId());
		LOG.debug("Outcome URL :: "+principal.getOutcome().get().getOutcomeURL());
		
		//printParameters(request);
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
