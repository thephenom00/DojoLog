export const mockData = {
  parent: {
    email: "parent@example.com",
    role: "ROLE_PARENT",
    firstName: "Demo",
    lastName: "Example",
    demo: true,
  },

  trainer: {
    email: "trainer@example.com",
    role: "ROLE_TRAINER",
    firstName: "Demo",
    lastName: "Example",
    demo: true,
  },

  schools: [
    {
      id: 1,
      name: "Demo School North",
    },
    {
      id: 2,
      name: "Demo School South",
    },
  ],

  trainings: [
    [
      {
        id: 1,
        name: "Začátečníci",
        dayOfWeek: "WEDNESDAY",
        startTime: [15, 0],
        endTime: [16, 30],
        price: 200,
        capacity: 10,
      },
    ],
    [
      {
        id: 2,
        name: "Pokročilí",
        dayOfWeek: "SATURDAY",
        startTime: [10, 0],
        endTime: [11, 30],
        price: 250,
        capacity: 8,
      },
    ],
  ],

  parentUpcomingTrainings: [
    {
      id: 1,
      date: "2025-05-14",
      dayOfTheWeek: "Středa",
      title: "Začátečníci",
      location: "Demo School North",
      time: "15:00 - 16:30",
      childNames: [],
      trainerNames: ["Alex Coach"],
      trainerPhoneNumbers: ["+420 000 000 000"]
    },
    {
      id: 2,
      date: "2025-05-17",
      dayOfTheWeek: "Sobota",
      title: "Pokročilí",
      location: "Demo School South",
      time: "10:00 - 11:30",
      childNames: [],
      trainerNames: ["Alex Coach, Sam Trainer"],
      trainerPhoneNumbers: ["+420 000 000 000", "+420 000 000 000"]
    },
  ],

    trainerUpcomingTrainings: [
    {
      id: 1,
      date: "2025-05-14",
      description: "",
      dayOfWeek: "WEDNESDAY",
      name: "Začátečníci",
      schoolName: "Demo School North",
      time: "15:00 - 16:30",
      numberOfChildren: 5,
      instructions:
        "Klíče od tělocvičny jsou u paní vrátné, vyzvedněte je před začátkem.",
      contactPerson: "School Contact A",
      contactNumber: "+420 000 000 000",
    },
    {
      id: 2,
      date: "2025-05-17",
      description: "",
      dayOfWeek: "SATURDAY",
      name: "Pokročilí",
      schoolName: "Demo School South",
      time: "10:00 - 11:30",
      numberOfChildren: 4,
      instructions:
        "Přístup do šatny je přes zadní vchod, klíče vyzvednout u školníka.",
      contactPerson: "School Contact B",
      contactNumber: "+420 000 000 000",
    },
  ],

  pastTrainings: [
    {
      id: 101,
      date: "2025-05-07",
      description:
        "Procvičení pádů vzad, ukázka techniky osoto otoshi a hry na závěr.",
      name: "Začátečníci",
      schoolName: "Demo School North",
      time: "15:00 - 16:30",
      dayOfTheWeek: "Středa"
    },
    {
      id: 102,
      date: "2025-04-30",
      description:
        "Zahřátí hrou mrazík, nácvik držení kesa gatame a technika tai-otoshi.",
      name: "Začátečníci",
      schoolName: "Demo School North",
      time: "15:00 - 16:30",
      dayOfTheWeek: "Středa"
    },
    {
      id: 201,
      date: "2025-05-10",
      description: "Randori s důrazem na přechody do newazy.",
      name: "Pokročilí",
      schoolName: "Demo School South",
      time: "10:00 - 11:30",
      dayOfTheWeek: "Sobota"
    },
    {
      id: 202,
      date: "2025-05-03",
      description:
        "Trénink zaměřený na kombinaci – kouchi gari a morote seoi nage.",
      name: "Pokročilí",
      schoolName: "Demo School South",
      time: "10:00 - 11:30",
      dayOfTheWeek: "Sobota"
    },
  ],

  childAttendances: {
    1: [
      { id: 1, firstName: "Casey", lastName: "Example", present: false },
      { id: 2, firstName: "Riley", lastName: "Sample", present: false },
      { id: 3, firstName: "Jordan", lastName: "Demo", present: false },
      { id: 4, firstName: "Taylor", lastName: "Test", present: false },
    ],
    2: [
      { id: 5, firstName: "Morgan", lastName: "Example", present: false },
      { id: 6, firstName: "Avery", lastName: "Sample", present: false },
      { id: 7, firstName: "Drew", lastName: "Demo", present: false },
      { id: 8, firstName: "Jamie", lastName: "Test", present: false },
      { id: 9, firstName: "Quinn", lastName: "Example", present: false },
    ],
  },

  trainerAttendances: {
    1: [{ id: 1, firstName: "Alex", lastName: "Coach", present: false }],
    2: [
      { id: 3, firstName: "Alex", lastName: "Coach", present: false },
      { id: 4, firstName: "Sam", lastName: "Trainer", present: false },
    ],
  },

  news: [
    {
      id: 1,
      name: "Aplikace oficiálně spuštěna",
      description:
        "S radostí oznamujeme, že naše nová aplikace pro správu docházky a tréninků je nyní plně v provozu. Děkujeme za důvěru a těšíme se na společnou sezónu!",
      date: [2025, 5, 24],
    },
  ],

  events: [
    {
      id: 1,
      name: "Soustředění Hranice",
      description:
        "Zveme všechny malé i velké judisty na letní judo soustředění, které se uskuteční v Hranicích na Moravě.\n\n Čeká nás intenzivní týden plný judistických tréninků, rozvoje techniky, taktiky i fyzické kondice.\n\n Každý den bude rozdělen do několika bloků, během kterých se zaměříme na zdokonalování základních i pokročilých technik.\n\nSoučástí programu budou také společné aktivity mimo tatami, budování týmového ducha a večerní program pro zábavu i odpočinek.\n\nTrenéři se budou individuálně věnovat každému účastníkovi, aby si každý odnesl co nejvíce zážitků i pokroku.\n\nTěšíme se na společný týden plný juda, pohybu, kamarádství a letní atmosféry!",
      eventType: "Soustředění",
      location: "Demo Sports Centre",
      startDate: "1.7.2025",
      endDate: "8.7.2025",
      startTime: "10:00",
      endTime: "18:00",
      places: 20,
      takenPlaces: 3,
      price: 2700,
      trainerSalary: 1199,
    },
  ],

  childrenInEvent: [
    {
      id: 2,
      name: "Riley Sample",
      note: "Demo note",
      paymentReceived: false,
      phoneNumber: "+420 000 000 000",
      email: "parent.one@example.com",
      present: false,
    },
    {
      id: 3,
      name: "Jordan Demo",
      note: "null",
      paymentReceived: false,
      phoneNumber: "+420 000 000 000",
      email: "parent.two@example.com",
      present: false,
    },
    {
      id: 4,
      name: "Taylor Test",
      note: "null",
      paymentReceived: false,
      phoneNumber: "+420 000 000 000",
      email: "parent.three@example.com",
      present: false,
    },
  ],

  reports: [
    {
      date: "30.4.2025",
      name: "Demo School North",
      dayOfWeek: "WEDNESDAY",
      startTime: [15, 0],
      endTime: [16, 30],
      hours: 1.5,
      type: "Training",
      money: 300,
    },
    {
      date: "3.5.2025",
      name: "Demo School South",
      dayOfWeek: "SATURDAY",
      startTime: [10, 0],
      endTime: [11, 30],
      hours: 1.5,
      type: "Training",
      money: 350,
    },
    {
      date: "7.5.2025",
      name: "Demo School North",
      dayOfWeek: "WEDNESDAY",
      startTime: [15, 0],
      endTime: [16, 30],
      hours: 1.5,
      type: "Training",
      money: 300,
    },
    {
      date: "10.5.2025",
      name: "Demo School South",
      dayOfWeek: "SATURDAY",
      startTime: [10, 0],
      endTime: [11, 30],
      hours: 1.5,
      type: "Training",
      money: 350,
    },
    {
      date: "14.5.2025",
      name: "Demo School North",
      dayOfWeek: "WEDNESDAY",
      startTime: [15, 0],
      endTime: [16, 30],
      hours: 1.5,
      type: "Training",
      money: 300,
    },
    {
      date: "17.5.2025",
      name: "Demo School South",
      dayOfWeek: "SATURDAY",
      startTime: [10, 0],
      endTime: [11, 30],
      hours: 1.5,
      type: "Training",
      money: 350,
    },
  ],
};
