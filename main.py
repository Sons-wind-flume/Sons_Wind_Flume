import csv
import math



# Step 1: Read sensor data
# For now, this pretends to be a live sensor feed by reading rows 
# from our sample spreadsheet. Update the column names once we actually opena the spreadsheet and see what's in it

def read_data_rows(filepath):
    # Reads rows from the sample spreadsheet ( CVS format) and returns them 
    # as a list of dictionaries [{'time' : 0,1. 'force_ref': 2.3, 'force_test': 1.8},...]

    rows = []
    with open(filepath, newline='') as f:
        reader = csv.DictReader(f)
        for row in reader:
            rows.append(row)
    return rows

# Step 3: The physics math ( F = 1/2 * rho * v^2 * Cd * A)

RHO_WATER = 1000  # kg/m^3, density of water (approx)

def calculate_velocity(force_ref, cd_ref, area_ref):
    #Using the Reference sphere ( known Cd and A), solve for velocity.
    #Rearranged: Cd = 2F / (rho * v^2 * A)
    
    return ( 2 * force_test) / (RHO_WATER * velocity ** 2 * area_test)


# Step 2: Log result to CVS

def write_results_csv(filepath, results):
    # Write calculate results out to a CVS file
    # 'result' is list of dicts
    # e.g [{'time' : 0.1, 'velocity': 1.2, 'cd_test' : 0.47},...]

    if not results:
        print("No results to write.")
        return
    
    with open(filepath, 'w', newline='') as f:
        writer = csv.DictWriter(f, fieldnames = results[0].keys())
        writer.writeheader()
        write.writerows(results)


#MAIN: write it own together

def main():
    #TO DO: update these once we confirm actual spreadsheet column names
    input_file = "sample_data.csv"
    output_file = "results.csv"

    #Know values for the reference sphere ( from spreadsheet/spec sheet)
    cd_ref = 0.47
    area_ref = 0.01
    area_text = 0.02

    data_rows = read_data_rows(input_file)
    results = []

    for row in data_rows:
        force_ref = float(row['force_ref']) # update column name as needed
        force_test = float(row['force_test']) # update column name a

        velocity = calculate_velocity(force_ref, cd_ref, area_ref)
        cd_test = calculate_unknown_cd(force_test, velocity, area_test)

        results.append({
            'time': row.get('time', ''),
            'velocity': round(velocity, 4),
            'cd_test': round(cd_test, 4)
        })

    write_results_csv(output_file, results)
    print(f"Done! Processed {len(result)} rows -> {output_file}")

if __name__ == "__main__":
    main()
