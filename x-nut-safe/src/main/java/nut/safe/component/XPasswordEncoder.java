package nut.safe.component;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class XPasswordEncoder extends BCryptPasswordEncoder {

}
