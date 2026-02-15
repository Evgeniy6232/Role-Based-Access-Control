public record Permission(String name, String resource, String description) {

    public Permission(String name, String resource, String description) {

        String normalizedName = name.toUpperCase();
        if (normalizedName.contains(" ")) {
            throw new IllegalArgumentException("Name cannot contain space");
        }

        String normalizedResource = resource.toLowerCase();
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Description field cannot be empty");
        }

        this.name = normalizedName;
        this.resource = normalizedResource;
        this.description = description;
    }

    public String format(){
        return String.format("%s on %s: %s", name, resource, description);
    }

    public boolean matches(String namePattern, String resourcePattern) {

        boolean nameMatches;
        if (namePattern == null || namePattern.isBlank()) {
            nameMatches = true;
        } else {

            nameMatches = this.name.contains(namePattern);
        }

        boolean resourceMatches;
        if(resourcePattern == null || resourcePattern.isBlank()) {
            resourceMatches = true;
        } else {
            resourceMatches = this.resource.contains(resourcePattern);
        }

        return nameMatches && resourceMatches;
    }

    public static void main(String[] args) {

        System.out.println("TESTS");

        Permission test1 = new Permission("read", "USERS", "Can read users");
        System.out.println(" " + test1.format());
        System.out.println(" name -> READ:  " + test1.name());
        System.out.println(" resource -> users:  " + test1.resource());
    }
}