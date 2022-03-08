package fr.spacefox.jrational;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

class RationalConstantsTest {

    private static final Rational PI_1000_DIGITS = Rational.of("3.14159265358979323846264338327950288419716939937510582"
                    + "097494459230781640628620899862803482534211706798214808651328230664709384460955058223172535940812"
                    + "848111745028410270193852110555964462294895493038196442881097566593344612847564823378678316527120"
                    + "190914564856692346034861045432664821339360726024914127372458700660631558817488152092096282925409"
                    + "171536436789259036001133053054882046652138414695194151160943305727036575959195309218611738193261"
                    + "179310511854807446237996274956735188575272489122793818301194912983367336244065664308602139494639"
                    + "522473719070217986094370277053921717629317675238467481846766940513200056812714526356082778577134"
                    + "275778960917363717872146844090122495343014654958537105079227968925892354201995611212902196086403"
                    + "441815981362977477130996051870721134999999837297804995105973173281609631859502445945534690830264"
                    + "252230825334468503526193118817101000313783875288658753320838142061717766914730359825349042875546"
                    + "8731159562863882353787593751957781857780532171226806613001927876611195909216420198")
            .canonicalForm();

    private static final Rational E_1000_DIGITS = Rational.of("2.71828182845904523536028747135266249775724709369995957"
                    + "496696762772407663035354759457138217852516642742746639193200305992181741359662904357290033429526"
                    + "059563073813232862794349076323382988075319525101901157383418793070215408914993488416750924476146"
                    + "066808226480016847741185374234544243710753907774499206955170276183860626133138458300075204493382"
                    + "656029760673711320070932870912744374704723069697720931014169283681902551510865746377211125238978"
                    + "442505695369677078544996996794686445490598793163688923009879312773617821542499922957635148220826"
                    + "989519366803318252886939849646510582093923982948879332036250944311730123819706841614039701983767"
                    + "932068328237646480429531180232878250981945581530175671736133206981125099618188159304169035159888"
                    + "851934580727386673858942287922849989208680582574927961048419844436346324496848756023362482704197"
                    + "862320900216099023530436994184914631409343173814364054625315209618369088870701676839642437814059"
                    + "2714563549061303107208510383750510115747704171898610687396965521267154688957035035")
            .canonicalForm();

    @Test
    void piTest() {
        assertNotEquals(0, RationalConstants.PI.compareTo(PI_1000_DIGITS));
        assertEquals(0, RationalConstants.PI.compareTo(PI_1000_DIGITS.approximate()));
        assertEquals(Math.PI, RationalConstants.PI.doubleValue());
    }

    @Test
    void eTest() {
        assertNotEquals(0, RationalConstants.E.compareTo(E_1000_DIGITS));
        assertEquals(0, RationalConstants.E.compareTo(E_1000_DIGITS.approximate()));
        assertEquals(Math.E, RationalConstants.E.doubleValue());
    }
}