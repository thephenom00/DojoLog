export const mockData = {
  parent: {
    email: "demo.parent@demo.cz",
    role: "ROLE_PARENT",
    firstName: "Demo",
    lastName: "Rodič",
    demo: true,
  },

  trainer: {
    email: "demo.trainer@demo.cz",
    role: "ROLE_TRAINER",
    firstName: "Demo",
    lastName: "Trenér",
    demo: true,
  },

  schools: [
    {
      id: 1,
      name: "2. ZŠ Plzeň",
    },
    {
      id: 2,
      name: "33. ZŠ Plzeň",
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
      location: "2. ZŠ Plzeň",
      time: "15:00 - 16:30",
      childNames: [],
      trainerNames: ["Denis Tesař"],
      trainerPhoneNumbers: [602433532]
    },
    {
      id: 2,
      date: "2025-05-17",
      dayOfTheWeek: "Sobota",
      title: "Pokročilí",
      location: "33. ZŠ Plzeň",
      time: "10:00 - 11:30",
      childNames: [],
      trainerNames: ["Denis Tesař, Lukáš Havel"],
      trainerPhoneNumbers: [602433532, 778843675]
    },
  ],

    trainerUpcomingTrainings: [
    {
      id: 1,
      date: "2025-05-14",
      description: "",
      dayOfWeek: "WEDNESDAY",
      name: "Začátečníci",
      schoolName: "2. ZŠ Plzeň",
      time: "15:00 - 16:30",
      numberOfChildren: 5,
      instructions:
        "Klíče od tělocvičny jsou u paní vrátné, vyzvedněte je před začátkem.",
      contactPerson: "Dominik Hájek",
      contactNumber: "+420 777 123 456",
    },
    {
      id: 2,
      date: "2025-05-17",
      description: "",
      dayOfWeek: "SATURDAY",
      name: "Pokročilí",
      schoolName: "33. ZŠ Plzeň",
      time: "10:00 - 11:30",
      numberOfChildren: 4,
      instructions:
        "Přístup do šatny je přes zadní vchod, klíče vyzvednout u školníka.",
      contactPerson: "Dominik Duchoň",
      contactNumber: "+420 605 987 654",
    },
  ],

  pastTrainings: [
    {
      id: 101,
      date: "2025-05-07",
      description:
        "Procvičení pádů vzad, ukázka techniky osoto otoshi a hry na závěr.",
      name: "Začátečníci",
      schoolName: "2. ZŠ Plzeň",
      time: "15:00 - 16:30",
      dayOfTheWeek: "Středa"
    },
    {
      id: 102,
      date: "2025-04-30",
      description:
        "Zahřátí hrou mrazík, nácvik držení kesa gatame a technika tai-otoshi.",
      name: "Začátečníci",
      schoolName: "2. ZŠ Plzeň",
      time: "15:00 - 16:30",
      dayOfTheWeek: "Středa"
    },
    {
      id: 201,
      date: "2025-05-10",
      description: "Randori s důrazem na přechody do newazy.",
      name: "Pokročilí",
      schoolName: "33. ZŠ Plzeň",
      time: "10:00 - 11:30",
      dayOfTheWeek: "Sobota"
    },
    {
      id: 202,
      date: "2025-05-03",
      description:
        "Trénink zaměřený na kombinaci – kouchi gari a morote seoi nage.",
      name: "Pokročilí",
      schoolName: "33. ZŠ Plzeň",
      time: "10:00 - 11:30",
      dayOfTheWeek: "Sobota"
    },
  ],

  childAttendances: {
    1: [
      { id: 1, firstName: "Eliška", lastName: "Novotná", present: false },
      { id: 2, firstName: "Matěj", lastName: "Horák", present: false },
      { id: 3, firstName: "Tereza", lastName: "Dvořáková", present: false },
      { id: 4, firstName: "Jakub", lastName: "Král", present: false },
    ],
    2: [
      { id: 5, firstName: "Adam", lastName: "Beneš", present: false },
      { id: 6, firstName: "Nikola", lastName: "Kučerová", present: false },
      { id: 7, firstName: "Filip", lastName: "Marek", present: false },
      { id: 8, firstName: "Lucie", lastName: "Fialová", present: false },
      { id: 9, firstName: "David", lastName: "Kolář", present: false },
    ],
  },

  trainerAttendances: {
    1: [{ id: 1, firstName: "Denis", lastName: "Tesař", present: false }],
    2: [
      { id: 3, firstName: "Denis", lastName: "Tesař", present: false },
      { id: 4, firstName: "Lukáš", lastName: "Havel", present: false },
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
      location: "Hranice na Moravě",
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
      name: "Matěj Horák",
      note: "'Alergie na laktózu'",
      paymentReceived: false,
      phoneNumber: "+420 774 773 772",
      email: "tomashorak@seznam.cz",
      present: false,
    },
    {
      id: 3,
      name: "Tereza Dvořáková",
      note: "null",
      paymentReceived: false,
      phoneNumber: "+420 775 333 372",
      email: "dvorak1979@gmail.com",
      present: false,
    },
    {
      id: 4,
      name: "Jakub Král",
      note: "null",
      paymentReceived: false,
      phoneNumber: "+420 609 123 567",
      email: "jirkakral@outlook.com",
      present: false,
    },
  ],

  reports: [
    {
      date: "30.4.2025",
      name: "2. ZŠ Plzeň",
      dayOfWeek: "WEDNESDAY",
      startTime: [15, 0],
      endTime: [16, 30],
      hours: 1.5,
      type: "Training",
      money: 300,
    },
    {
      date: "3.5.2025",
      name: "33. ZŠ Plzeň",
      dayOfWeek: "SATURDAY",
      startTime: [10, 0],
      endTime: [11, 30],
      hours: 1.5,
      type: "Training",
      money: 350,
    },
    {
      date: "7.5.2025",
      name: "2. ZŠ Plzeň",
      dayOfWeek: "WEDNESDAY",
      startTime: [15, 0],
      endTime: [16, 30],
      hours: 1.5,
      type: "Training",
      money: 300,
    },
    {
      date: "10.5.2025",
      name: "33. ZŠ Plzeň",
      dayOfWeek: "SATURDAY",
      startTime: [10, 0],
      endTime: [11, 30],
      hours: 1.5,
      type: "Training",
      money: 350,
    },
    {
      date: "14.5.2025",
      name: "2. ZŠ Plzeň",
      dayOfWeek: "WEDNESDAY",
      startTime: [15, 0],
      endTime: [16, 30],
      hours: 1.5,
      type: "Training",
      money: 300,
    },
    {
      date: "17.5.2025",
      name: "33. ZŠ Plzeň",
      dayOfWeek: "SATURDAY",
      startTime: [10, 0],
      endTime: [11, 30],
      hours: 1.5,
      type: "Training",
      money: 350,
    },
  ],
};
