package br.com.Kofflix.KFX.models;


public enum Categoria {
    ACAO("Action"),
    ROMANCE("Romance"),
    COMEDIA("Comedy"),
    TERROR("Horror"),
    DRAMA("Drama"),
    CRIME("Crime"),
    ANIMACAO("Animation"),
    OUTROS("Other"); ;

    private String categoriaOmdb;

    Categoria(String categoriaOmdb){
        this.categoriaOmdb = categoriaOmdb;
    }

    public static Categoria fromString(String text) {
        for (Categoria categoria : Categoria.values()) {
            if (categoria.categoriaOmdb.equalsIgnoreCase(text)) {
                return categoria;
            }
        }
        System.out.println("⚠️ Categoria não mapeada: " + text);
            return OUTROS;
    }
}
