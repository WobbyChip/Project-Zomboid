// 
// Decompiled by Procyon v0.5.36
// 

package zombie.util;

import java.util.GregorianCalendar;
import java.util.Date;
import java.util.Objects;
import java.util.Calendar;

public final class PZCalendar
{
    private final Calendar calendar;
    
    public static PZCalendar getInstance() {
        return new PZCalendar(Calendar.getInstance());
    }
    
    public PZCalendar(final Calendar calendar) {
        Objects.requireNonNull(calendar);
        this.calendar = calendar;
    }
    
    public void set(final int year, final int month, final int date, final int hourOfDay, final int minute) {
        this.calendar.set(year, month, date, hourOfDay, minute);
    }
    
    public void setTimeInMillis(final long timeInMillis) {
        this.calendar.setTimeInMillis(timeInMillis);
    }
    
    public int get(final int field) {
        return this.calendar.get(field);
    }
    
    public final Date getTime() {
        return this.calendar.getTime();
    }
    
    public long getTimeInMillis() {
        return this.calendar.getTimeInMillis();
    }
    
    public boolean isLeapYear(final int year) {
        return ((GregorianCalendar)this.calendar).isLeapYear(year);
    }
}
