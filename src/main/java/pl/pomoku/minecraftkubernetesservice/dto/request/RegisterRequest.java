package pl.pomoku.minecraftkubernetesservice.dto.request;

public record RegisterRequest(String firstName, String lastName, String email, String phone, String password) {
}
