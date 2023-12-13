package hcmute.it.furnitureshop.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RevenueRoom implements Comparable<RevenueRoom>{
    private String roomName;
    private Double revenue;
    @Override
    public int compareTo(RevenueRoom o) {
        return o.getRevenue().compareTo(this.revenue);
    }
}
