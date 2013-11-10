package humanize;

import static humanize.Humanize.binaryPrefix;
import static humanize.Humanize.camelize;
import static humanize.Humanize.capitalize;
import static humanize.Humanize.decamelize;
import static humanize.Humanize.duration;
import static humanize.Humanize.fixLength;
import static humanize.Humanize.formatCurrency;
import static humanize.Humanize.formatDate;
import static humanize.Humanize.formatDateTime;
import static humanize.Humanize.formatDecimal;
import static humanize.Humanize.formatPercent;
import static humanize.Humanize.mask;
import static humanize.Humanize.metricPrefix;
import static humanize.Humanize.nanoTime;
import static humanize.Humanize.naturalDay;
import static humanize.Humanize.naturalTime;
import static humanize.Humanize.ordinal;
import static humanize.Humanize.pluralize;
import static humanize.Humanize.pluralizeFormat;
import static humanize.Humanize.prettyTimeFormat;
import static humanize.Humanize.replaceSupplementary;
import static humanize.Humanize.slugify;
import static humanize.Humanize.spellBigNumber;
import static humanize.Humanize.spellDigit;
import static humanize.Humanize.titleize;
import static humanize.Humanize.transliterate;
import static humanize.Humanize.underscore;
import static humanize.Humanize.unmask;
import static humanize.Humanize.wordWrap;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;
import humanize.spi.MessageFormat;
import humanize.time.Pace;
import humanize.time.PrettyTimeFormat;
import humanize.util.Constants.TimeMillis;
import humanize.util.Constants.TimeStyle;

import java.math.BigInteger;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import org.junit.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class TestHumanize
{

	private static final Locale ES = new Locale("es", "ES");

	private Random rand;

	private Locale defaultLocale;

	@Test(threadPoolSize = 10, invocationCount = 10)
	public void binPrefixTest()
	{

		assertEquals(binaryPrefix(-1), "-1");
		assertEquals(binaryPrefix(0), "0 bytes");
		assertEquals(binaryPrefix(1), "1 bytes");
		assertEquals(binaryPrefix(101), "101 bytes");
		assertEquals(binaryPrefix(1025), "1 kB");
		assertEquals(binaryPrefix(1024), "1 kB");
		assertEquals(binaryPrefix(1536), "1.5 kB");
		assertEquals(binaryPrefix(1048576 * 5), "5 MB");
		assertEquals(binaryPrefix(1073741824L * 2), "2 GB");
		assertEquals(binaryPrefix(1099511627776L * 3), "3 TB");
		assertEquals(binaryPrefix(1325899906842624L), "1.18 PB");

		assertEquals(binaryPrefix(1325899906842624L, ES), "1,18 PB");

	}

	@Test
	public void camelizeTest()
	{

		assertEquals(camelize("bla bla_bla "), "blaBlaBla");
		assertEquals(camelize("  blA_blA  Bla", true), "BlaBlaBla");
		assertEquals(camelize("bla_bla!"), "blaBla!");
		assertEquals(camelize("bla-bla!"), "blaBla!");
		assertEquals(camelize("bla.bla!"), "blaBla!");
		assertEquals(camelize("xxx"), "xxx");
		assertEquals(camelize("___"), "___");
		assertEquals(camelize(" "), " ");
		assertEquals(camelize(" _ _ _"), " _ _ _");
		assertEquals(camelize(""), "");
		assertEquals(camelize("xxx", true), "Xxx");
		assertEquals(camelize("alreadyCamelized"), "alreadyCamelized");

		try
		{
			camelize(null);
			fail("handles null?");
		} catch (NullPointerException ex)
		{

		}

	}

	@Test
	public void capitalizeTest()
	{

		assertEquals(capitalize("hola mundo abc"), "Hola mundo abc");
		assertEquals(capitalize("HOLA mundO AbC"), "Hola mundo abc");
		assertEquals(capitalize("Hola Mundo abC"), "Hola mundo abc");
		assertEquals(capitalize(""), "");
		assertEquals(capitalize("* Hola Mundo aBC"), "* hola mundo abc");

		assertEquals(capitalize("iyi akşamlar", new Locale("tr")), "İyi akşamlar");

		try
		{
			capitalize(null);
			fail("handles null?");
		} catch (NullPointerException ex)
		{

		}

	}

	@Test(threadPoolSize = 10, invocationCount = 10)
	public void decamelizeTest()
	{

		assertEquals(decamelize("lowercase"), "lowercase");
		assertEquals(decamelize("Class"), "Class");
		assertEquals(decamelize("MyClass"), "My Class");
		assertEquals(decamelize("HTML"), "HTML");
		assertEquals(decamelize("PDFLoader"), "PDF Loader");
		assertEquals(decamelize("AString"), "A String");
		assertEquals(decamelize("SimpleXMLParser"), "Simple XML Parser");
		assertEquals(decamelize("GL11Version"), "GL 11 Version");

	}

	@Test
	public void durationFrenchTest()
	{

		assertEquals(duration(3600, TimeStyle.FRENCH_DECIMAL), "1h");
		assertEquals(duration(3661, TimeStyle.FRENCH_DECIMAL), "1h 1m 1s");

		assertEquals(duration(-7200, TimeStyle.FRENCH_DECIMAL), "-2h");
		assertEquals(duration(7200, TimeStyle.FRENCH_DECIMAL), "2h");
		assertEquals(duration(7214, TimeStyle.FRENCH_DECIMAL), "2h 14s");
		assertEquals(duration(7261, TimeStyle.FRENCH_DECIMAL), "2h 1m 1s");
		assertEquals(duration(-7261, TimeStyle.FRENCH_DECIMAL), "-2h 1m 1s");

		assertEquals(duration(0, TimeStyle.FRENCH_DECIMAL), "0s");
		assertEquals(duration(10, TimeStyle.FRENCH_DECIMAL), "10s");
		assertEquals(duration(-10, TimeStyle.FRENCH_DECIMAL), "-10s");
		assertEquals(duration(60, TimeStyle.FRENCH_DECIMAL), "1m");
		assertEquals(duration(61, TimeStyle.FRENCH_DECIMAL), "1m 1s");
		assertEquals(duration(120, TimeStyle.FRENCH_DECIMAL), "2m");
		assertEquals(duration(125, TimeStyle.FRENCH_DECIMAL), "2m 5s");
		assertEquals(duration(2015, TimeStyle.FRENCH_DECIMAL), "33m 35s");

	}

	@Test
	public void durationStandardTest()
	{

		assertEquals(duration(3600), "1:00:00");
		assertEquals(duration(3661), "1:01:01");

		assertEquals(duration(-7200), "-2:00:00");
		assertEquals(duration(-1799), "-0:29:59");
		assertEquals(duration(1799), "0:29:59");
		assertEquals(duration(7200), "2:00:00");
		assertEquals(duration(7214), "2:00:14");
		assertEquals(duration(-7214), "-2:00:14");
		assertEquals(duration(7261), "2:01:01");

		assertEquals(duration(0), "0:00:00");
		assertEquals(duration(10), "0:00:10");
		assertEquals(duration(60), "0:01:00");
		assertEquals(duration(61), "0:01:01");
		assertEquals(duration(120), "0:02:00");
		assertEquals(duration(125), "0:02:05");
		assertEquals(duration(2015), "0:33:35");

	}

	@Test
	public void fixLengthTest()
	{

		assertEquals(fixLength("", 5, '-'), "-----");
		assertEquals(fixLength(null, 1, '-'), "-");
		assertEquals(fixLength("abc", 1, '-'), "a");
		assertEquals(fixLength("12345", 10, '-'), "12345-----");
		assertEquals(fixLength("12345", 3, '-'), "123");
		assertEquals(fixLength("12345", 10, '-', true), "-----12345");
		assertEquals(fixLength("12345", 3, '-', true), "123");

		try
		{
			assertEquals(fixLength("12345", 0, '-'), "123");
			Assert.fail();
		} catch (Exception e)
		{
			//
		}

	}

	@Test(threadPoolSize = 10, invocationCount = 10)
	public void formatCurrencyTest()
	{

		int df = rand.nextInt(9);
		assertEquals(formatCurrency(34), "£34");
		assertEquals(formatCurrency(1000 + df), "£1,00" + df);
		assertEquals(formatCurrency(10000.55 + df), "£10,00" + df + ".55");

		assertEquals(formatCurrency(100, ES), "100 €");
		assertEquals(formatCurrency(1000.55, ES), "1.000,55 €");

	}

	@Test(threadPoolSize = 10, invocationCount = 10)
	public void formatDateTest()
	{

		int day = rand.nextInt(20) + 1;
		Date date = newTestDate(day, 11, 2015);

		assertEquals(formatDate(DateFormat.MEDIUM, date), String.format("%02d-Dec-2015", day));
		assertEquals(formatDate(date), String.format("%02d/12/15", day));

		assertEquals(formatDate(DateFormat.MEDIUM, date, ES), String.format("%02d-dic-2015", day));
		assertEquals(formatDate(date, ES), String.format("%d/12/15", day));

		assertEquals(formatDate(date, "dd/MM/yy"), String.format("%02d/12/15", day));
		assertEquals(formatDate(date, "dd/MM/yy", ES), String.format("%02d/12/15", day));

	}

	@Test(threadPoolSize = 10, invocationCount = 10)
	public void formatDateTimeTest()
	{

		int day = rand.nextInt(20) + 1;
		Date date = newTestDate(day, 11, 2015, 22, 10, 0);

		assertEquals(formatDateTime(DateFormat.MEDIUM, DateFormat.MEDIUM, date),
		        String.format("%02d-Dec-2015 22:10:00", day));
		assertEquals(formatDateTime(date), String.format("%02d/12/15 22:10", day));

		assertEquals(formatDateTime(DateFormat.MEDIUM, DateFormat.MEDIUM, date, ES),
		        String.format("%02d-dic-2015 22:10:00", day));
		assertEquals(formatDateTime(date, ES), String.format("%d/12/15 22:10", day));

	}

	@Test(threadPoolSize = 10, invocationCount = 10)
	public void formatDecimalTest()
	{

		int df = rand.nextInt(9);
		assertEquals(formatDecimal(1000 + df), "1,00" + df);
		assertEquals(formatDecimal(10000.55 + df), "10,00" + df + ".55");
		assertEquals(formatDecimal(1000 + df, ES), "1.00" + df);

	}

	@Test(threadPoolSize = 10, invocationCount = 10)
	public void formatPercentTest()
	{

		assertEquals(formatPercent(0), "0%");
		assertEquals(formatPercent(-1), "-100%");
		assertEquals(formatPercent(0.5), "50%");
		assertEquals(formatPercent(1.5), "150%");
		assertEquals(formatPercent(0.564), "56%");
		assertEquals(formatPercent(1000.564), "100,056%");

		assertEquals(formatPercent(0, ES), "0%");
		assertEquals(formatPercent(-1, ES), "-100%");
		assertEquals(formatPercent(0.5, ES), "50%");
		assertEquals(formatPercent(1.5, ES), "150%");
		assertEquals(formatPercent(0.564, ES), "56%");
		assertEquals(formatPercent(1000.564, ES), "100.056%");

	}

	@Test(threadPoolSize = 10, invocationCount = 10)
	public void maskFormatTest() throws ParseException
	{

		int r = rand.nextInt(9);
		assertEquals(mask("_-__-_____/_", "A5881850" + r), "A-58-81850/" + r);
		assertEquals(mask("_# __ _____#-_", "A/5881850 " + r), "A 58 81850-" + r);
		assertEquals(mask("__ ____ _", r + "01100A"), r + "0 1100 A");
		assertEquals(unmask("__ ____ _", r + "0 1100 A"), r + "01100A");

	}

	@Test(threadPoolSize = 10, invocationCount = 10)
	public void metricPrefixTest()
	{

		assertEquals(metricPrefix(-1), "-1");
		assertEquals(metricPrefix(0), "0");
		assertEquals(metricPrefix(1), "1");
		assertEquals(metricPrefix(101), "101");
		assertEquals(metricPrefix(1000), "1k");
		assertEquals(metricPrefix(1000000), "1M");
		assertEquals(metricPrefix(3500000), "3.5M");

		assertEquals(metricPrefix(3500000, ES), "3,5M");

	}

	@Test(threadPoolSize = 10, invocationCount = 10)
	public void nanoTimeTest()
	{

		assertEquals(nanoTime(0), "0ns");
		assertEquals(nanoTime(1500), "1.5µs");
		assertEquals(nanoTime(1000), "1µs");
		assertEquals(nanoTime(100000), "100µs");
		assertEquals(nanoTime(1000000), "1ms");
		assertEquals(nanoTime(15400000), "15.4ms");
		assertEquals(nanoTime(5000000000L), "5s");
		assertEquals(nanoTime(550150000000L), "550.15s");

		// within locale
		assertEquals(nanoTime(15400000, ES), "15,4ms");

	}

	@Test(threadPoolSize = 10, invocationCount = 10)
	public void naturalDayTest()
	{

		Calendar cal = Calendar.getInstance();
		assertEquals(naturalDay(cal.getTime()), "today");
		assertEquals(naturalDay(cal.getTime(), ES), "hoy");
		cal.add(Calendar.DATE, 1);
		assertEquals(naturalDay(cal.getTime()), "tomorrow");
		assertEquals(naturalDay(cal.getTime(), ES), "mañana");
		cal.add(Calendar.DAY_OF_MONTH, -2);
		assertEquals(naturalDay(cal.getTime()), "yesterday");
		assertEquals(naturalDay(cal.getTime(), ES), "ayer");
		cal.add(Calendar.DAY_OF_WEEK, -1);
		assertEquals(naturalDay(cal.getTime()), formatDate(cal.getTime()));

	}

	@Test(threadPoolSize = 10, invocationCount = 10)
	public void naturalTimeTest()
	{

		assertEquals(naturalTime(new Date(0), new Date(1)), "moments from now");

		assertEquals(naturalTime(new Date(0), new Date(1000 * 60 * 12)), "12 minutes from now");
		assertEquals(naturalTime(new Date(0), new Date(1000 * 60 * 60 * 3)), "3 hours from now");
		assertEquals(naturalTime(new Date(0), new Date(1000 * 60 * 60 * 24 * 1)), "1 day from now");
		assertEquals(naturalTime(new Date(0), new Date(1000 * 60 * 60 * 24 * 3)), "3 days from now");
		assertEquals(naturalTime(new Date(0), new Date(1000 * 60 * 60 * 24 * 7 * 3)), "3 weeks from now");
		assertEquals(naturalTime(new Date(0), new Date(2629743830L * 3L)), "3 months from now");
		assertEquals(naturalTime(new Date(0), new Date(2629743830L * 13L * 3L)), "3 years from now");
		assertEquals(naturalTime(new Date(0), new Date(315569259747L * 3L)), "3 decades from now");
		assertEquals(naturalTime(new Date(0), new Date(3155792597470L * 3L)), "3 centuries from now");

		assertEquals(naturalTime(new Date(6000), new Date(0)), "moments ago");
		assertEquals(naturalTime(new Date(1000 * 60 * 12), new Date(0)), "12 minutes ago");
		assertEquals(naturalTime(new Date(1000 * 60 * 60 * 3), new Date(0)), "3 hours ago");
		assertEquals(naturalTime(new Date(1000 * 60 * 60 * 24 * 1), new Date(0)), "1 day ago");
		assertEquals(naturalTime(new Date(1000 * 60 * 60 * 24 * 3), new Date(0)), "3 days ago");
		assertEquals(naturalTime(new Date(1000 * 60 * 60 * 24 * 7 * 3), new Date(0)), "3 weeks ago");
		assertEquals(naturalTime(new Date(2629743830L * 3L), new Date(0)), "3 months ago");
		assertEquals(naturalTime(new Date(2629743830L * 13L * 3L), new Date(0)), "3 years ago");
		assertEquals(naturalTime(new Date(315569259747L * 3L), new Date(0)), "3 decades ago");
		assertEquals(naturalTime(new Date(3155792597470L * 3L), new Date(0)), "3 centuries ago");

		// within locale
		assertEquals(naturalTime(new Date(0), new Date(1), ES), "en un instante");
		assertEquals(naturalTime(new Date(1), new Date(0), ES), "hace instantes");
		assertEquals(naturalTime(new Date(0), new Date(1000 * 60 * 12), ES), "dentro de 12 minutos");
		assertEquals(naturalTime(new Date(1000 * 60 * 60 * 24 * 1), new Date(0), ES), "hace 1 día");

	}

	@Test(threadPoolSize = 10, invocationCount = 10)
	public void ordinalTest()
	{

		assertEquals(ordinal(0), "0th");
		assertEquals(ordinal(1), "1st");
		assertEquals(ordinal(2), "2nd");
		assertEquals(ordinal(3), "3rd");
		assertEquals(ordinal(4), "4th");
		assertEquals(ordinal(5), "5th");
		assertEquals(ordinal(33), "33rd");
		assertEquals(ordinal(11), "11th");
		assertEquals(ordinal(12), "12th");
		assertEquals(ordinal(13), "13th");
		assertEquals(ordinal(10), "10th");
		assertEquals(ordinal(22), "22nd");
		assertEquals(ordinal(101), "101st");
		assertEquals(ordinal(-10), "-10th");
		assertEquals(ordinal(1.25), "1st");
		assertEquals(ordinal(new Float(1.33)), "1st");
		assertEquals(ordinal(new Long(10000000)), "10000000th");

		assertEquals(ordinal(1, ES), "1º");

	}

	@Test(threadPoolSize = 5, invocationCount = 5)
	public void paceCustomFormatTest()
	{
		assertEquals(Humanize.paceFormat(2.5, 1000, "{1} {0} heartbeat per {2}.", "{1} {0} heartbeats per {2}.",
		        "No heartbeats."), "Approximately 3 heartbeats per second.");
		assertEquals(Humanize.paceFormat(ES, 2.5, 1000, "{1} un latido por {2}.", "{1} {0} latidos por {2}.",
		        "Sin latidos."), "Aproximadamente 3 latidos por segundo.");
	}

	@Test(threadPoolSize = 10, invocationCount = 10)
	public void paceFormatTest()
	{
		assertEquals(Humanize.paceFormat(2.5, 1000), "Approximately 3 times per second.");
		assertEquals(Humanize.paceFormat(5, 86405000), "Approximately 5 times per day.");
		assertEquals(Humanize.paceFormat(2000.5, 1000), "Approximately 2,001 times per second.");
		assertEquals(Humanize.paceFormat(1, TimeMillis.WEEK.millis() * 10), "Less than one time per month.");
		assertEquals(Humanize.paceFormat(1, TimeMillis.WEEK.millis()), "Approximately one time per week.");

		assertEquals(Humanize.paceFormat(ES, 1.5, 86400000), "Aproximadamente 2 veces por día.");
		assertEquals(Humanize.paceFormat(ES, 2000.5, 1000), "Aproximadamente 2.001 veces por segundo.");
	}

	@Test
	public void paceTest()
	{

		Pace args = Humanize.pace(1.5, 1000);

		assertEquals(String.format("%s %s %s", args.getAccuracy(), args.getValue(), args.getTimeUnit()),
		        "aprox 2 second");

		args = Humanize.pace(1, 1000);
		assertEquals(String.format("%s %s %s", args.getAccuracy(), args.getValue(), args.getTimeUnit()),
		        "aprox 1 second");
		
		args = Humanize.pace(-1, -1000);
		assertEquals(String.format("%s %s %s", args.getAccuracy(), args.getValue(), args.getTimeUnit()),
		        "aprox 1 second");

		args = Humanize.pace(1, 60000);
		assertEquals(String.format("%s %s %s", args.getAccuracy(), args.getValue(), args.getTimeUnit()),
		        "aprox 1 minute");

		args = Humanize.pace(0.5, 60000);
		assertEquals(String.format("%s %s %s", args.getAccuracy(), args.getValue(), args.getTimeUnit()),
		        "aprox 1 minute");

		args = Humanize.pace(0, 1000);
		assertEquals(String.format("%s %s %s", args.getAccuracy(), args.getValue(), args.getTimeUnit()),
		        "none 0 second");

		args = Humanize.pace(0.2, 1000);
		assertEquals(String.format("%s %s %s", args.getAccuracy(), args.getValue(), args.getTimeUnit()),
		        "none 0 second");

		args = Humanize.pace(-7.6, 1000);
		assertEquals(String.format("%s %s %s", args.getAccuracy(), args.getValue(), args.getTimeUnit()),
		        "aprox 8 second");

		args = Humanize.pace(7.6, TimeMillis.DAY.millis());
		assertEquals(String.format("%s %s %s", args.getAccuracy(), args.getValue(), args.getTimeUnit()),
		        "aprox 8 day");

	}

	@Test(threadPoolSize = 10, invocationCount = 10)
	public void pluralizeFormatSimpleTest()
	{

		String pattern = "{0}";
		String none = "{0} things";
		String one = "one thing";
		String many = "{0} things";

		MessageFormat f = pluralizeFormat(pattern, none, one, many);

		assertEquals(f.render(0), "0 things");
		assertEquals(f.render(-1), "-1 things");
		assertEquals(f.render(1), "one thing");
		assertEquals(f.render(2), "2 things");

	}

	@Test(threadPoolSize = 10, invocationCount = 10)
	public void pluralizeFormatTest()
	{

		int df = rand.nextInt(9);

		String pattern = "There {0} on {1}.";
		String none = "are no files";
		String one = "is one file";
		String many = "are {2} files";

		MessageFormat f = pluralizeFormat(pattern, none, one, many);

		assertEquals(f.render(1000 + df, "disk", 1000 + df), "There are 1,00" + df + " files on disk.");
		assertEquals(f.render(0, "disk"), "There are no files on disk.");
		assertEquals(f.render(-1, "disk"), "There are no files on disk.");
		assertEquals(f.render(1, "disk"), "There is one file on disk.");
		assertEquals(f.render(1, "disk", 1000, "bla bla"), "There is one file on disk.");

	}

	@Test(threadPoolSize = 10, invocationCount = 10)
	public void pluralizeFormatWithSimpleTemplate()
	{

		MessageFormat f = pluralizeFormat("{0}::nothing::one thing::{0} things");

		assertEquals(f.render(0), "nothing");
		assertEquals(f.render(-1), "nothing");
		assertEquals(f.render(1), "one thing");
		assertEquals(f.render(2), "2 things");

		f = pluralizeFormat("nothing::one thing::{0} things");

		assertEquals(f.render(0), "nothing");
		assertEquals(f.render(-1), "nothing");
		assertEquals(f.render(1), "one thing");
		assertEquals(f.render(2), "2 things");

		f = pluralizeFormat("one thing::{0} things");

		assertEquals(f.render(0), "0 things");
		assertEquals(f.render(-1), "-1 things");
		assertEquals(f.render(1), "one thing");
		assertEquals(f.render(2), "2 things");

	}

	@Test(threadPoolSize = 10, invocationCount = 10)
	public void pluralizeFormatWithTemplate()
	{

		int df = rand.nextInt(9);

		MessageFormat f = pluralizeFormat("There {0} on {1}. :: are no files::is one file::  are {2} files");

		assertEquals(f.render(1000 + df, "disk", 1000 + df), "There are 1,00" + df + " files on disk.");
		assertEquals(f.render(0, "disk"), "There are no files on disk.");
		assertEquals(f.render(-1, "disk"), "There are no files on disk.");
		assertEquals(f.render(1, "disk"), "There is one file on disk.");
		assertEquals(f.render(1, "disk", 1000, "bla bla"), "There is one file on disk.");

		f = pluralizeFormat("{0}.::No hay ficheros::Hay un fichero::Hay {0,number} ficheros", ES);
		assertEquals(f.render(0), "No hay ficheros.");
		assertEquals(f.render(1), "Hay un fichero.");
		assertEquals(f.render(2000), "Hay 2.000 ficheros.");

		try
		{
			pluralizeFormat("---");
			fail("incorrect number of tokens");
		} catch (IllegalArgumentException ex)
		{

		}

	}

	@Test(threadPoolSize = 10, invocationCount = 10)
	public void pluralizeNoExtTest()
	{

		int df = rand.nextInt(9);

		String none = "There are no files.";
		String one = "There is one file.";
		String many = "There are {0} files.";

		assertEquals(pluralize(one, many, 2 + df), "There are " + (2 + df) + " files.");
		assertEquals(pluralize(one, many, 1), "There is one file.");
		assertEquals(pluralize(one, many, 0), "There are 0 files.");
		assertEquals(pluralize(one, many, none, 2 + df), "There are " + (2 + df) + " files.");
		assertEquals(pluralize(one, many, none, 0), "There are no files.");
		assertEquals(pluralize(one, many, none, 1), "There is one file.");

		assertEquals(pluralize("one", "{0}", "none", 1), "one");
		assertEquals(pluralize("one", "{0}", "none", 0), "none");
		assertEquals(pluralize("one", "{0}", "none", 2), "2");

	}

	@Test(threadPoolSize = 10, invocationCount = 10)
	public void pluralizeTest()
	{

		int df = rand.nextInt(9);

		String none = "There are no files on {1}.";
		String one = "There is one file on {1}.";
		String many = "There are {0} files on {1}.";

		assertEquals(pluralize(one, many, 2 + df, "disk"), "There are " + (2 + df) + " files on disk.");
		assertEquals(pluralize(one, many, 1, "disk"), "There is one file on disk.");
		assertEquals(pluralize(one, many, 0, "disk"), "There are 0 files on disk.");
		assertEquals(pluralize(one, many, none, 2 + df, "disk"), "There are " + (2 + df) + " files on disk.");
		assertEquals(pluralize(one, many, none, 0, "disk"), "There are no files on disk.");
		assertEquals(pluralize(one, many, none, 1, "disk"), "There is one file on disk.");

	}

	@Test(threadPoolSize = 10, invocationCount = 10)
	public void prettyTimeFormatTest()
	{

		PrettyTimeFormat prettyTime = prettyTimeFormat();
		assertEquals(prettyTime.format(new Date(0), new Date(1000 * 60 * 12)), "12 minutes from now");
		assertEquals(prettyTime.format(new Date(0), new Date(1000 * 60 * 60 * 3)), "3 hours from now");
		assertEquals(prettyTime.format(new Date(0), new Date(1000 * 60 * 60 * 24 * 1)), "1 day from now");
		assertEquals(prettyTime.format(new Date(0), new Date(1000 * 60 * 60 * 24 * 3)), "3 days from now");
		assertEquals(prettyTime.format(new Date(0), new Date(1000 * 60 * 60 * 24 * 7 * 3)), "3 weeks from now");
		assertEquals(prettyTime.format(new Date(2629743830L * 3L), new Date(0)), "3 months ago");
		assertEquals(prettyTime.format(new Date(2629743830L * 13L * 3L), new Date(0)), "3 years ago");
		assertEquals(prettyTime.format(new Date(315569259747L * 3L), new Date(0)), "3 decades ago");
		assertEquals(prettyTime.format(new Date(3155792597470L * 3L), new Date(0)), "3 centuries ago");

		// within locale
		prettyTime = prettyTimeFormat(ES);
		assertEquals(prettyTime.format(new Date(0), new Date(1)), "en un instante");

	}

	@Test
	public void replaceSupplementaryTest()
	{

		assertEquals(
		        replaceSupplementary("The first three letters of the Gothic alphabet are: \uD800\uDF30 \uD800\uDF31 \uD800\uDF32 and not"),
		        "The first three letters of the Gothic alphabet are: \\uD800\\uDF30 \\uD800\\uDF31 \\uD800\\uDF32 and not");

		assertEquals(replaceSupplementary("A normal string"), "A normal string");

		// Emoji face
		assertEquals(replaceSupplementary(new StringBuilder().appendCodePoint(0x1F60A).toString()), "\\uD83D\\uDE0A");

	}

	@Test
	public void slugifyTest()
	{

		assertEquals(slugify("J'étudie le français"), "jetudie-le-francais");
		assertEquals(slugify("Cet été, j’en ai rien à coder"), "cet-ete-jen-ai-rien-a-coder");
		assertEquals(slugify("Lo siento, no hablo español."), "lo-siento-no-hablo-espanol");
		assertEquals(slugify("ïúàôéÏÚÀÔÉĆ-ężĶ- ..Šűa͠    	a̸"), "iuaoeiuaoec-ezk-sua-a");

	}

	@Test(threadPoolSize = 10, invocationCount = 10)
	public void spellBigNumberTest()
	{

		assertEquals(spellBigNumber(100), "100");
		assertEquals(spellBigNumber(2300), "2.3 thousand");
		assertEquals(spellBigNumber(1000000), "1 million");
		assertEquals(spellBigNumber(1300000), "1.3 million");
		assertEquals(spellBigNumber(1000000000), "1 billion");
		assertEquals(spellBigNumber(1550000001), "1.55 billion");
		assertEquals(spellBigNumber(-1550000001), "-1.55 billion");
		assertEquals(spellBigNumber(BigInteger.TEN.pow(33).multiply(BigInteger.valueOf(3))), "3 decillion");
		assertEquals(spellBigNumber(BigInteger.TEN.pow(100).multiply(BigInteger.valueOf(2))), "2 googol");
		BigInteger ultraBig = BigInteger.TEN.pow(1000);
		assertEquals(spellBigNumber(ultraBig), ultraBig.toString());

		// within locale
		assertEquals(spellBigNumber(100, ES), "100");
		assertEquals(spellBigNumber(2300, ES), "2,3 miles");
		assertEquals(spellBigNumber(1000000, ES), "1 millón");
		assertEquals(spellBigNumber(1300000, ES), "1,3 millones");
		assertEquals(spellBigNumber(1000000000, ES), "1 millardo");
		assertEquals(spellBigNumber(1550000001, ES), "1,55 millardos");
		assertEquals(spellBigNumber(-1550000001, ES), "-1,55 millardos");

	}

	@Test(threadPoolSize = 10, invocationCount = 10)
	public void spellDigitTest()
	{

		assertEquals(spellDigit(1), "one");
		assertEquals(spellDigit(3), "three");
		assertEquals(spellDigit(0), "zero");
		assertEquals(spellDigit(10), "10");
		assertEquals(spellDigit(-1), "-1");
		assertEquals(spellDigit(9), "nine");

		assertEquals(spellDigit(1, ES), "uno");
		assertEquals(spellDigit(9, ES), "nueve");

	}

	@Test(threadPoolSize = 10, invocationCount = 10)
	public void titleizeTest()
	{

		assertEquals(titleize("the_jackie_gleason show"), "The Jackie Gleason Show");
		assertEquals(titleize("first annual report (CD) 2001"), "First Annual Report (CD) 2001");

	}

	@Test
	public void transliterateTest()
	{

		assertEquals(transliterate("J'étudie le français"), "J'etudie le francais");
		assertEquals(transliterate("Lo siento, no hablo español."), "Lo siento, no hablo espanol.");
		assertEquals(transliterate("ïúàôéÏÚÀÔÉĆężĶŠűa͠a̸"), "iuaoeIUAOECezKSuaa");

	}

	@Test
	public void undescoreTest()
	{

		assertEquals(underscore("a bunch of  macarios"), "a_bunch_of_macarios");
		assertEquals(underscore(" "), "_");
		assertEquals(underscore(""), "");
		assertEquals(underscore("one_two__three"), "one_two__three");

	}

	@Test(threadPoolSize = 10, invocationCount = 10)
	public void wordWrapTest()
	{

		int df = rand.nextInt(9);

		String phrase = "Lorem ipsum dolorem si amet, lorem ipsum. Dolorem sic et nunc." + df;
		assertEquals(wordWrap(phrase, 2), "Lorem");
		assertEquals(wordWrap(phrase, 30), "Lorem ipsum dolorem si amet, lorem");
		assertEquals(wordWrap(phrase, phrase.length()), phrase);
		assertEquals(wordWrap(phrase, phrase.length() * 2), phrase);
		assertEquals(wordWrap(phrase, 0), "Lorem");
		assertEquals(wordWrap(phrase, -2), phrase);

	}

	@BeforeClass
	void setUp()
	{

		defaultLocale = Locale.getDefault();
		Locale.setDefault(Locale.UK);
		rand = new Random();

	}

	@AfterClass
	void tearDown()
	{

		Locale.setDefault(defaultLocale);
	}

	private Date newTestDate(int day, int month, int year)
	{

		return newTestDate(day, month, year, 0, 0, 0);

	}

	private Date newTestDate(int day, int month, int year, int h, int m, int s)
	{

		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DATE, day);
		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.HOUR_OF_DAY, h);
		cal.set(Calendar.MINUTE, m);
		cal.set(Calendar.SECOND, s);
		return cal.getTime();

	}

}
