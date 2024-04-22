package pl.pomoku.minecraftkubernetesservice.service;

import org.springframework.stereotype.Service;

import java.util.function.Predicate;
import java.util.regex.Pattern;

@Service
public class EmailValidator implements Predicate<String> {
    private final static Pattern pattern = Pattern.compile("^(.+)@(\\\\S+)$");
    @Override
    public boolean test(String s) {
        return pattern.matcher(s).matches();
    }
}
