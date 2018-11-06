package org.drools.compiler.integrationtests;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.drools.core.impl.UnitExecutor;
import org.drools.testcoverage.common.model.Person;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.runtime.rule.DataSource;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.runtime.rule.RuleUnit;
import org.kie.api.runtime.rule.RuleUnitExecutor;

import static org.drools.core.util.ClassUtils.getCanonicalSimpleName;
import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class RuleUnitTest2 {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public RuleUnitTest2(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(true);
    }
    /*

<mfusco_> the first units has 2 rules
<mfusco_> let's call them R1A and R1B
<mfusco_> the second unit has only R2A
<mfusco_> R1A fires and activates the guards for R2
<mfusco_> but since it's a guard not a run the control remains to the first unit
<mfusco_> then R1B fires and retract the condition that made R1A to fire
<mfusco_> at that point also the guard should be retracted
<mfusco_> and R2A shouldn't fire at all
<mfusco_> with me?
<evacchi> yes i think so
<mfusco_> what I said on Tue is that the first firing of R1A will add the guard and it will be never removed
<evacchi> we need to verify if that's the same behavior that we have in our current impl
<mfusco_> so if I'm right in this scenario I expect that R2A will fire even if it shouldn't
<mfusco_> agreed, I have no clue if this works with current impl
<mfusco_> and since it is not covered by tests very likely it won't :)
<mfusco_> so this is a useful check also for old impl

     */

    @Test
    public void testDoublyGuardedUnit() {
        final String drl0 =
                "package org.drools.compiler.integrationtests\n" +
                        "unit " + getCanonicalSimpleName(CinemaUnit.class) + ";\n" +
                        "import " + Cinema.class.getCanonicalName() + "\n" +
                        "import " + BoxOfficeUnit.class.getCanonicalName() + "\n" +
                        "\n" +
                        "rule CinemaIsOpen when\n" +
                        "    $str: String( this == \"foo\" )\n" +
                        "    $box: /cinema[ open ]\n" +
                        "then\n" +
                        "    System.out.println(\"CINEMA is open!\");\n" +
                        "    drools.guard( BoxOfficeUnit.class );\n" +
                        "end";

        final String drl1 =
                "package org.drools.compiler.integrationtests\n" +
                        "unit " + getCanonicalSimpleName(BoxOfficeUnit.class) + ";\n" +
                        "import " + BoxOffice.class.getCanonicalName() + "\n" +
                        "import " + TicketIssuerUnit.class.getCanonicalName() + "\n" +
                        "\n" +
                        "rule BoxOfficeIsOpen no-loop when\n" +
                        "    $str: String( )\n" +
                        "    $box: /boxOffices[ open ]\n" +
                        "then\n" +
                        "    System.out.println(\"Box office is open!\");\n" +
                        "    drools.guard( TicketIssuerUnit.class );\n" +
//                        "    delete($str);"+
                        "    insert(\"bar\");"+
                        "end";
//                        "\n" +
//                        "rule BoxOfficeGetsClosed when\n" +
//                        "    $s:   String( )\n" +
//                        "    $box: /boxOffices[ open ]\n" +
//                        "then\n" +
//                        "    System.out.println(\"Box office will close.\");\n" +
//                        "    modify( $box ) { setOpen( false ) };" +
//                        "end";

        final String drl2 =
                "package org.drools.compiler.integrationtests\n" +
                        "unit " + getCanonicalSimpleName(TicketIssuerUnit.class) + ";\n" +
                        "import " + Person.class.getCanonicalName() + "\n" +
                        "import " + AdultTicket.class.getCanonicalName() + "\n" +
                        "rule IssueAdultTicket when\n" +
                        "    $p: /persons[ age >= 18 ]\n" +
                        "then\n" +
                        "    tickets.insert(new AdultTicket($p));\n" +
                        "end\n" +
                        "rule RegisterAdultTicket when\n" +
                        "    $t: /tickets\n" +
                        "then\n" +
                        "    results.add( $t.getPerson().getName() );\n" +
                        "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("rule-unit-test", kieBaseTestConfiguration, drl0, drl1, drl2);
        final RuleUnitExecutor executor =
//                RuleUnitExecutor.create().bind(kbase);
                UnitExecutor.create().bind(kbase);
        try {
            executor.getKieSession().insert("foo");
            final DataSource<Person> persons = executor.newDataSource("persons");
            final DataSource<BoxOffice> boxOffices = executor.newDataSource("boxOffices");
            executor.newDataSource("tickets");

            final List<String> list = new ArrayList<>();
            executor.bindVariable("results", list);

            // two open box offices
            final BoxOffice office1 = new BoxOffice(true);
            final FactHandle officeFH1 = boxOffices.insert(office1);
            final BoxOffice office2 = new BoxOffice(true);
            final FactHandle officeFH2 = boxOffices.insert(office2);

            persons.insert(new Person("Mario", 40));
            executor.run(BoxOfficeUnit.class); // fire BoxOfficeIsOpen -> run TicketIssuerUnit -> fire RegisterAdultTicket

            assertEquals(1, list.size());
        } finally {
            executor.dispose();
        }
    }    @Test
    public void test2DoublyGuardedUnit() {
        final String drl0 =
                "package org.drools.compiler.integrationtests\n" +
                        "unit " + getCanonicalSimpleName(CinemaUnit.class) + ";\n" +
                        "import " + Cinema.class.getCanonicalName() + "\n" +
                        "import " + BoxOfficeUnit.class.getCanonicalName() + "\n" +
                        "import " + TicketIssuerUnit.class.getCanonicalName() + "\n" +
                        "\n" +
                        "rule CinemaIsOpen when\n" +
                        "    $str: String( )\n" +
                        "    $box: /cinema[ open ]\n" +
                        "then\n" +
                        "    System.out.println(\"CINEMA is open!\");\n" +
                        "    drools.run( BoxOfficeUnit.class );\n" +
                        "    drools.guard( TicketIssuerUnit.class );\n" +
                        "end";

        final String drl1 =
                "package org.drools.compiler.integrationtests\n" +
                        "unit " + getCanonicalSimpleName(BoxOfficeUnit.class) + ";\n" +
                        "import " + BoxOffice.class.getCanonicalName() + "\n" +
                        "import " + TicketIssuerUnit.class.getCanonicalName() + "\n" +
                        "\n" +
                        "rule BoxOfficeIsOpen no-loop when\n" +
                        "    $str: String( )\n" +
                        "    $box: /boxOffices[ open ]\n" +
                        "then\n" +
                        "    System.out.println(\"Box office is open!\");\n" +
                        "    drools.guard( TicketIssuerUnit.class );\n" +
                        "    delete($str);\n" +
                        "end";
//                        "\n" +
//                        "rule BoxOfficeGetsClosed when\n" +
//                        "    $s:   String( )\n" +
//                        "    $box: /boxOffices[ open ]\n" +
//                        "then\n" +
//                        "    System.out.println(\"Box office will close.\");\n" +
//                        "    modify( $box ) { setOpen( false ) };" +
//                        "end";

        final String drl2 =
                "package org.drools.compiler.integrationtests\n" +
                        "unit " + getCanonicalSimpleName(TicketIssuerUnit.class) + ";\n" +
                        "import " + Person.class.getCanonicalName() + "\n" +
                        "import " + AdultTicket.class.getCanonicalName() + "\n" +
                        "rule IssueAdultTicket when\n" +
                        "    $p: /persons[ age >= 18 ]\n" +
                        "then\n" +
                        "    tickets.insert(new AdultTicket($p));\n" +
                        "end\n" +
                        "rule RegisterAdultTicket when\n" +
                        "    $t: /tickets\n" +
                        "then\n" +
                        "    results.add( $t.getPerson().getName() );\n" +
                        "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("rule-unit-test", kieBaseTestConfiguration, drl0, drl1, drl2);
        final RuleUnitExecutor executor =
//                RuleUnitExecutor.create().bind(kbase);
                UnitExecutor.create().bind(kbase);
        try {
            executor.getKieSession().insert("foo");
            final DataSource<Person> persons = executor.newDataSource("persons");
            final DataSource<BoxOffice> boxOffices = executor.newDataSource("boxOffices");
            executor.newDataSource("tickets");

            final List<String> list = new ArrayList<>();
            executor.bindVariable("results", list);

            // two open box offices
            final BoxOffice office1 = new BoxOffice(true);
            final FactHandle officeFH1 = boxOffices.insert(office1);
            final BoxOffice office2 = new BoxOffice(true);
            final FactHandle officeFH2 = boxOffices.insert(office2);

            persons.insert(new Person("Mario", 40));
            executor.run(CinemaUnit.class); // fire BoxOfficeIsOpen -> run TicketIssuerUnit -> fire RegisterAdultTicket

            assertEquals(0, list.size());
        } finally {
            executor.dispose();
        }
    }

    public static class Cinema {
        private boolean open = true;

        public boolean isOpen() {
            return open;
        }

        public void setOpen(boolean open) {
            this.open = open;
        }
    }

    public static class BoxOffice {

        private boolean open;

        public BoxOffice(final boolean open) {
            this.open = open;
        }

        public boolean isOpen() {
            return open;
        }

        public void setOpen(final boolean open) {
            this.open = open;
        }
    }

    public static class AdultTicket {

        private final Person person;

        public AdultTicket(final Person person) {
            this.person = person;
        }

        public Person getPerson() {
            return person;
        }
    }

    public static class CinemaUnit implements RuleUnit {

        private DataSource<Cinema> cinema;

        public DataSource<Cinema> getCinema() {
            return cinema;
        }
    }


    public static class BoxOfficeUnit implements RuleUnit {

        private DataSource<BoxOffice> boxOffices;

        public DataSource<BoxOffice> getBoxOffices() {
            return boxOffices;
        }
    }

    public static class TicketIssuerUnit implements RuleUnit {

        private DataSource<Person> persons;
        private DataSource<AdultTicket> tickets;

        private List<String> results;

        public TicketIssuerUnit() {
        }

        public TicketIssuerUnit(final DataSource<Person> persons, final DataSource<AdultTicket> tickets) {
            this.persons = persons;
            this.tickets = tickets;
        }

        public DataSource<Person> getPersons() {
            return persons;
        }

        public DataSource<AdultTicket> getTickets() {
            return tickets;
        }

        public List<String> getResults() {
            return results;
        }
    }
}
