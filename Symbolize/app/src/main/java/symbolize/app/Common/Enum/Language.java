package symbolize.app.Common.Enum;

public enum Language {
    English( 1 ), EN( 1 ), French( 2 ), FR( 2 ), Spanish( 3 ), ES( 3 ), German( 4 ), DE( 4 ),
    Italian( 5 ), IT( 5 ), Dutch( 6 ), NL( 6 ), Portuguese( 7 ), PT( 7 ), Russian( 8 ), RU( 8 );
    private int value;

    private Language( int value ) {
        this.value = value;
    }

    public int Get_value() {
        return value;
    }
}
