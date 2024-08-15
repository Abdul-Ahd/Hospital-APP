package com.example.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.gis.UserCheck;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link dManager#newInstance} factory method to
 * create an instance of this fragment.
 */
public class dManager extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public dManager() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment dManager.
     */
    // TODO: Rename and change types and number of parameters
    public static dManager newInstance(String param1, String param2) {
        dManager fragment = new dManager();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ImageButton button = view.findViewById(R.id.patientbutton);
        ImageButton disease = view.findViewById(R.id.diseaseButton);
        ImageButton data = view.findViewById(R.id.RecordButton);
        ImageButton hosptl = view.findViewById(R.id.HospitalButton);
        ImageButton docEffort = view.findViewById(R.id.docEffort);
        ImageButton discharge = view.findViewById(R.id.dischargeBtn);
        ImageButton viewMapButton = view.findViewById(R.id.viewMapBtn);
        ImageButton manageRecordButton = view.findViewById(R.id.manageRecordBtn);
        ImageButton reportButton = view.findViewById(R.id.ReportViewBtn);
        TextView addPatientText = view.findViewById(R.id.addPatientText);
        TextView addDiseaseText = view.findViewById(R.id.addDiseaseText);
        TextView addHospitalText = view.findViewById(R.id.addHospitalText);
        TextView addRecordText = view.findViewById(R.id.addRecordText);
        TextView docEffortText = view.findViewById(R.id.doceffortText);
        TextView mapview = view.findViewById(R.id.viewMap);
        TextView MRecord = view.findViewById(R.id.manageRecord);
        TextView report = view.findViewById(R.id.ReportView);
        TextView barchart = view.findViewById(R.id.BarChart);
        view.findViewById(R.id.treatment).setVisibility(View.GONE);
        view.findViewById(R.id.patDischarge).setVisibility(View.GONE);


        if(UserCheck.PERF_ROLE.equals("Doctor")){
           disease.setVisibility(View.GONE);
           addDiseaseText.setVisibility(View.GONE);
           hosptl.setVisibility(View.GONE);
           addHospitalText.setVisibility(View.GONE);

           view.findViewById(R.id.treatment).setVisibility(View.VISIBLE);
           view.findViewById(R.id.patDischarge).setVisibility(View.VISIBLE);
        }
        if(UserCheck.PERF_ROLE.equals("Simple User")){
            button.setVisibility(View.GONE);
            addPatientText.setVisibility(View.GONE);
            disease.setVisibility(View.GONE);
            addDiseaseText.setVisibility(View.GONE);
            data.setVisibility(View.GONE);
            addRecordText.setVisibility(View.GONE);
            hosptl.setVisibility(View.GONE);
            addHospitalText.setVisibility(View.GONE);
            mapview.setVisibility(View.GONE);
            MRecord.setVisibility(View.GONE);
            report.setVisibility(View.GONE);
            docEffort.setVisibility(View.GONE);
            docEffortText.setVisibility(View.GONE);
        }
        UserCheck userCheck = new UserCheck(getContext());
        userCheck.getRoles(getContext(), new UserCheck.RolesCallback() {
            @Override
            public void onRolesRetrieved(String[] roles) {
//                String[] rolesArray = roles;
                for (String role : roles) {
                    if (role.equals("Add Disease")) {
                        disease.setVisibility(View.VISIBLE);
                        addDiseaseText.setVisibility(View.VISIBLE);
                    } else if (role.equals("Add Patient")) {
                        button.setVisibility(View.VISIBLE);
                        addPatientText.setVisibility(View.VISIBLE);
                    } else if (role.equals("Add Record")) {
                        data.setVisibility(View.VISIBLE);
                        addRecordText.setVisibility(View.VISIBLE);
                    } else if (role.equals("Add Hospital")) {
                        hosptl.setVisibility(View.VISIBLE);
                        addHospitalText.setVisibility(View.VISIBLE);
                    } else if (role.equals("View Record")) {
                        mapview.setVisibility(View.VISIBLE);
                    } else if (role.equals("Change Record")) {
                        MRecord.setVisibility(View.VISIBLE);
                    } else if (role.equals("View Report")) {
                        report.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Patient.class);

                startActivity(intent);
            }
        });


        disease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Disease.class);

                startActivity(intent);
            }
        });

        data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Patient_Found.class);

                startActivity(intent);
            }
        });


        hosptl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Hospital.class);

                startActivity(intent);
            }
        });

        viewMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), mapbox.class);

                startActivity(intent);
            }
        });

        manageRecordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Manage_Record.class);

                startActivity(intent);
            }
        });

        reportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), RecordReport.class);

                startActivity(intent);
            }
        });
        barchart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MYBarChart.class);

                startActivity(intent);
            }
        });
        docEffort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), doctor_effort.class);

                startActivity(intent);
            }
        });
        discharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), discharge.class);

                startActivity(intent);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_d_manager, container, false);
    }
}